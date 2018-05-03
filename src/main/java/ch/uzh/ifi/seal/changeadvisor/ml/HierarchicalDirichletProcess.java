package ch.uzh.ifi.seal.changeadvisor.ml;

import cc.mallet.util.Maths;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.Topic;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicAssignment;
import ch.uzh.ifi.seal.changeadvisor.ml.math.Multinomial;
import ch.uzh.ifi.seal.changeadvisor.ml.math.SimpleMultinomial;
import ch.uzh.ifi.seal.changeadvisor.ml.math.Vector;
import ch.uzh.ifi.seal.changeadvisor.ml.util.DefaultMap;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by alex on 24.07.2017.
 */
public class HierarchicalDirichletProcess implements TopicInferencer, TopicAssigner {

    private static final Logger logger = LoggerFactory.getLogger(HierarchicalDirichletProcess.class);

    private Corpus corpus;

    private Vocabulary vocabulary;

    /**
     * Number of tokens in corpus. self._V in hdplda.py
     */
    private int vocabularySize;

    /**
     * Number of documents in corpus. self._M in hdplda.py
     */
    private int corpusSize;

    /**
     * t : table index for document j; t=0 means to draw a new table
     */
    private List<List<Integer>> usingT;

    /**
     * k : dish (topic) index; k=0 means to draw a new dish
     */
    private List<Integer> usingK;

    /**
     * Vocabulary for each document and term.
     */
    private List<List<Integer>> xJi;

    /**
     * Topics of document and table.
     */
    private List<List<Integer>> kJt;

    /**
     * Number of terms for each table of document.
     */
    private List<List<Integer>> nJt;

    private List<List<Map<Integer, Integer>>> nJtv;

    private int m;

    /**
     * Number of tables for each topic.
     */
    private List<Integer> mK;

    /**
     * Number of terms for each topic (+ beta * V).
     */
    private List<Double> nK;

    /**
     * Number of terms for each topic and vocabulary (+ beta).
     */
    private List<Map<Integer, Double>> nKv;

    /**
     * Table for each document and term (-1 means not-assigned).
     */
    private List<List<Integer>> tJi;

    private double alpha = 1.0;

    private double beta = 0.5;

    private double gamma = 1.0;

    private Multinomial multinomial = new SimpleMultinomial();

    public HierarchicalDirichletProcess(double alpha, double beta, double gamma) {
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
    }

    private void setup(List<List<Integer>> documentIds, int vocabSize) {
        vocabularySize = vocabSize;
        corpusSize = documentIds.size();

        usingT = new ArrayList<>(corpusSize);
        for (int i = 0; i < corpusSize; i++) {
            usingT.add(Lists.newArrayList(0));
        }

        usingK = Lists.newArrayList(0);

        xJi = documentIds;

        kJt = new ArrayList<>(corpusSize);
        nJt = new ArrayList<>(corpusSize);
        for (int i = 0; i < corpusSize; i++) {
            kJt.add(Lists.newArrayList(0));
            nJt.add(Lists.newArrayList(0));
        }

        nJtv = new ArrayList<>();
        for (int i = 0; i < corpusSize; i++) {
            nJtv.add(Lists.newArrayList((Map<Integer, Integer>) null));
        }

        m = 0;

        mK = Lists.newArrayList(1);

        nK = Lists.newArrayList(beta * vocabSize);

        nKv = Lists.newArrayList(new DefaultMap<>(0.0));

        tJi = new ArrayList<>(corpusSize);
        for (List<Integer> document : documentIds) {
            List<Integer> zeros = new ArrayList<>();
            for (int i = 0; i < document.size(); i++) {
                zeros.add(-1);
            }
            tJi.add(zeros);
        }
    }

    public void fit(Corpus corpus, int maxIterations) {
        this.corpus = corpus;
        vocabulary = new Vocabulary(corpus);

        setup(vocabulary.getDocumentIds(), vocabulary.vocabularySize());

        logger.info("=============== =============== ===============");
        logger.info("Iteration       Topics          Perplexity");
        logger.info("=============== =============== ===============");


        for (int i = 0; i < maxIterations; i++) {
            inference();
            logger.info(String.format("%-15d %-15d %-15f", i + 1, usingK.size() - 1, perplexity()));
            logger.info("=============== =============== ===============");
        }
    }

    public List<Topic> topics() {
        List<Topic> topics = new ArrayList<>(); //        topics = list()

        int topicNumber = 0;                //        topic_number = 0
        List<DefaultMap<Integer, Double>> phi = topicWordDistribution(); //        phi = self.topic_word_distribution()

        for (DefaultMap<Integer, Double> phiK : phi) {          //        for k, phi_k in enumerate(phi):
            List<Integer> indexes = phiK.getIndexOfTopNValues(20);
            List<String> words = indexes.stream().map(idx -> vocabulary.getVocab(idx)).collect(Collectors.toList());    //        words = [self._vocabulary[w] for w in sorted(phi_k, key=lambda w: -phi_k[w])[:20]]
            topics.add(new Topic(new HashSet<>(words), topicNumber++)); //        topics.append([topic_number, words])
        }
        return topics;//        return topics
    }

    public List<TopicAssignment> assignments() {
        List<Vector<Double>> documentTopicDistribution = documentTopicDistribution();   //        document_topic_distribution = self.document_topic_distribution()
        List<TopicAssignment> assignments = new ArrayList<>();            //        assignments = list()

        List<List<String>> documents = corpus.getDocuments();       //        documents = self._corpus.tokens()
        for (int i = 0; i < documentTopicDistribution.size(); i++) {
            Vector<Double> distribution = documentTopicDistribution.get(i).subVector(1);
            List<String> document = documents.get(i);
            int assignment = distribution.argmax();
            assignments.add(new TopicAssignment(corpus.getSentence(i), new HashSet<>(document), assignment));
        }
        return assignments;
    }

    private double perplexity() {
        List<DefaultMap<Integer, Double>> phi = Lists.newArrayList(new DefaultMap<>(1.0 / vocabularySize)); //phi = [DefaultDict(1.0 / self._V)] + self.topic_word_distribution()
        List<DefaultMap<Integer, Double>> topicsDist = topicWordDistribution();
        phi.addAll(topicsDist);
        List<Vector<Double>> theta = documentTopicDistribution(); //        theta = self.document_topic_distribution()

        double logLikelihood = 0.0;     //        log_likelihood = 0
        int n = 0;                      //        N = 0

        int minSize = Math.min(xJi.size(), theta.size());
        for (int i = 0; i < minSize; i++) {                         //        for xJi, p_jk in zip(self._x_ji, theta):
            List<Integer> x_ji = this.xJi.get(i);
            Vector<Double> p_jk = theta.get(i);
            for (Integer v : x_ji) {                                //        for v in xJi:
                int minSize2 = Math.min(p_jk.size(), phi.size());
                double sum = 0.0;
                for (int j = 0; j < minSize2; j++) {                //        word_prob = sum(p * p_kv[v] for p, p_kv in zip(p_jk, phi))
                    Double p = p_jk.get(j);
                    Double p_kv = phi.get(j).get(v);
                    sum += p * p_kv;
                }
                Double wordProb = sum;
                logLikelihood -= Math.log(wordProb);                //        log_likelihood -= numpy.log(word_prob)
            }
            n += x_ji.size();                                       //        N += len(xJi)
        }

        if (n == 0) {
            throw new IllegalArgumentException(
                    String.format("Something went wrong..can't divide by 0 in the next statement. MinSize: %d", minSize));
        }
        return Math.exp(logLikelihood / n);                                   //        return numpy.exp(log_likelihood / N)
    }

    private List<Vector<Double>> documentTopicDistribution() {
        // am_k = effect from table-dish assignment
        Vector<Double> amK = Vector.toDoubleVector(mK);         // am_k = numpy.array(self._m_k, dtype=float)
        amK.set(0, gamma);                                      // am_k[0] = self._gamma

        double sum = amK.get(usingK).sum();
        amK = amK.times(alpha / sum);                           // am_k *= self._alpha / am_k[self._using_k].sum()

        List<Vector<Double>> theta = new ArrayList<>();             //        theta = []

        for (int j = 0; j < nJt.size(); j++) {                     //        for j, nJt in enumerate(self._n_jt):
            Vector<Double> pJk = amK.copy();                       //        p_jk = am_k.copy()
            List<Integer> n_jt = this.nJt.get(j);
            for (Integer t : usingT.get(j)) {                       //        for t in self._using_t[j]:
                if (t == 0) {
                    continue;
                }
                Integer k = kJt.get(j).get(t);                     //                k = self._k_jt[j][t]
                pJk.set(k, pJk.get(k) + n_jt.get(t));             //        p_jk[k] += nJt[t]
            }
            pJk = pJk.get(usingK);                                //        p_jk = p_jk[self._using_k]
            theta.add(pJk.dividedBy(pJk.sum()));                  //        theta.append(p_jk / p_jk.sum())
        }
        return theta;           //        return numpy.array(theta)
    }

    private List<DefaultMap<Integer, Double>> topicWordDistribution() {
//        return [DefaultDict(self._beta / self._n_k[k]).update(
//                (v, n_kv / self._n_k[k]) for v, n_kv in self._n_kv[k].items())
//        for k in self._using_k if k != 0]
        List<DefaultMap<Integer, Double>> result = new ArrayList<>(usingK.size());
        DefaultMap<Integer, Double> topicWordDistribution;
        for (Integer k : usingK) {
            if (k != 0) {
                topicWordDistribution = new DefaultMap<>(1.0 / vocabularySize);
                result.add(topicWordDistribution);
                for (Map.Entry<Integer, Double> entry : nKv.get(k).entrySet()) {
                    Integer v = entry.getKey();
                    Double nKv = entry.getValue();

                    topicWordDistribution.put(v, nKv / nK.get(k));
                }
            }
        }
        return result;
    }

    private void inference() {
        for (int j = 0; j < xJi.size(); j++) {
            List<Integer> xi = xJi.get(j);
            for (int i = 0; i < xi.size(); i++) {
                sampling_t(j, i);
            }
        }

        for (int j = 0; j < corpusSize; j++) {
            for (Integer t : usingT.get(j)) {
                if (t != 0) {
                    sampling_k(j, t);
                }
            }
        }
    }

    private void sampling_t(int j, int i) {
        leaveFromTable(j, i);               // self._leave_from_table(j, i)
        Integer v = xJi.get(j).get(i);     // v = self._x_ji[j][i]
        Vector<Double> fk = calcFK(v);        // f_k = self._calc_f_k(v)

        if (fk.get(0) != 0) {   // assert f_k[0] == 0  # f_k[0] is a dummy and will be erased
            throw new IllegalArgumentException(String.format("j: %d, i: %d, fk.get(0): %f", j, i, fk.get(0)));
        }

        // Sampling from posterior p(t_ji = t).
        Vector<Double> pT = calcTablePosterior(j, fk);            // p_t = self._calc_table_posterior(j, f_k)

        multinomial.init(pT.get());
        Integer tNew = usingT.get(j).get(multinomial.sample()); // t_new = self._using_t[j][numpy.random.multinomial(1, p_t).argmax()]

        if (tNew == 0) {
            Vector<Double> pK = calcDishPosteriorW(fk);           // p_k = self._calc_dish_posterior_w(f_k)
            multinomial.init(pK.get());
            Integer kNew = usingK.get(multinomial.sample());    // k_new = self._using_k[numpy.random.multinomial(1, p_k).argmax()]

            if (kNew == 0) {                                    // if k_new == 0:
                kNew = addNewDish();                                // k_new = self._add_new_dish()
            }
            tNew = addNewTable(j, kNew);                        // t_new = self._add_new_table(j, k_new)
        }

        // Increases counters.
        seatAtTable(j, i, tNew);                                // self._seat_at_table(j, i, t_new)
    }

    /**
     * Sampling k dishes (topics) from posterior.
     *
     * @param j
     * @param t
     */
    private void sampling_k(int j, Integer t) {

        leaveFromDish(j, t); //        self._leave_from_dish(j, t)

        // Sampling of k.
        Vector<Double> pK = calcDishPosteriorT(j, t);//                p_k = self._calc_dish_posterior_t(j, t)
        for (Double p : pK) {
            if (p < 0) {
                logger.info(pK.toString());
                throw new IllegalArgumentException("How?!?");
            }
        }

        multinomial.init(pK.get());
        Integer kNew = usingK.get(multinomial.sample());    //        k_new = self._using_k[numpy.random.multinomial(1, p_k).argmax()]
        if (kNew == 0) {                                    //        if k_new == 0:
            kNew = addNewDish();                            //              k_new = self._add_new_dish()
        }

        seatAtDish(j, t, kNew);                             //        self._seat_at_dish(j, t, k_new)
    }

    private void seatAtDish(int j, Integer t, Integer kNew) {
        m += 1;
        mK.set(kNew, mK.get(kNew) + 1);

        Integer kOld = kJt.get(j).get(t);
        if (!kNew.equals(kOld)) {
            kJt.get(j).set(t, kNew);

            Integer n_jt = this.nJt.get(j).get(t);
            if (kOld != 0) {
                nK.set(kOld, nK.get(kOld) - n_jt);
            }
            nK.set(kNew, nK.get(kNew) + n_jt);

            for (Map.Entry<Integer, Integer> entry : nJtv.get(j).get(t).entrySet()) {
                Integer v = entry.getKey();
                Integer n = entry.getValue();
                if (kOld != 0) {
                    nKv.get(kOld).put(v, nKv.get(kOld).get(v) - n);
                }
                nKv.get(kNew).put(v, nKv.get(kNew).get(v) + n);
            }
        }
    }

    /**
     * Calculates dish (topic) posterior when one table is removed.
     *
     * @param j
     * @param t
     */
    private Vector<Double> calcDishPosteriorT(int j, Integer t) {
        Integer kOld = kJt.get(j).get(t);                      // k_old = self._k_jt[j][t]  # it may be zero (means a removed dish)

        double vBeta = vocabularySize * beta;                   //        Vbeta = self._V * self._beta
        Vector<Double> nk = new Vector<>(nK);                   //        n_k = self._n_k.copy()
        Integer n_jt = this.nJt.get(j).get(t);                 //        nJt = self._n_jt[j][t]
        nk.set(kOld, nk.get(kOld) - n_jt);                      //        n_k[k_old] -= nJt

        nk = nk.get(usingK);                                    //        n_k = n_k[self._using_k]

        Vector<Integer> tmp = new Vector<>(mK).get(usingK);

        Vector<Double> logPK = tmp.log().plus(nk.logGamma().minus(nk.plus(n_jt).logGamma())); //logMkUsingK.plus(gammaLnNk.minus(gammaLnNkNJt));                         //        log_p_k = numpy.log(self._m_k[self._using_k]) + gammaln(n_k) - gammaln(n_k + nJt)

        double logPKNew = Math.log(gamma) + Maths.logGamma(vBeta) - Maths.logGamma(vBeta + n_jt); //        log_p_k_new = numpy.log(self._gamma) + gammaln(Vbeta) - gammaln(Vbeta + nJt)

        double gammaLnBeta = Maths.logGamma(beta);                                                  //        gammaln_beta = gammaln(self._beta)
        for (Map.Entry<Integer, Integer> entry : nJtv.get(j).get(t).entrySet()) {  //        for w, n_jtw in self._n_jtv[j][t].items():
            Integer w = entry.getKey();
            Integer nJtw = entry.getValue();

            if (nJtw < 0) {//        assert n_jtw >= 0
                throw new IllegalArgumentException(String.format("j: %d\tt: %d,\tnJtw: %d", j, t, nJtw));
            }
            if (nJtw == 0) {                                                       //        if n_jtw == 0: continue
                continue;
            }

            Vector<Double> nKw = new Vector<>(nKv.stream().map(n -> n.getOrDefault(w, beta)).collect(Collectors.toList()));    //                n_kw = numpy.array([n.get(w, self._beta) for n in self._n_kv])
            nKw.set(kOld, nKw.get(kOld) - nJtw);                                 //        n_kw[k_old] -= n_jtw
            nKw = nKw.get(usingK);                                                //        n_kw = n_kw[self._using_k]
            nKw.set(0, 1.0);                                                       //        n_kw[0] = 1  # dummy for logarithm's warning

            // NOT TRANSPILED: if numpy.any(n_kw <= 0): print(n_kw) # for debug

            logPK = logPK.plus(nKw.plus(nJtw).logGamma().minus(nKw.logGamma())); // log_p_k += gammaln(n_kw + n_jtw) - gammaln(n_kw)

            logPKNew += Maths.logGamma(beta + nJtw) - gammaLnBeta;             //        log_p_k_new += gammaln(self._beta + n_jtw) - gammaln_beta
        }

        logPK.set(0, logPKNew);     //        log_p_k[0] = log_p_k_new

        Vector<Double> pK = Vector.exp(logPK.plus(-logPK.max())); //        p_k = numpy.exp(log_p_k - log_p_k.max())

        return pK.dividedBy(pK.sum());     //        return p_k / p_k.sum()
    }

    /**
     * Makes the table leave from its dish and only the table counter decrease.
     * The word counters (n_k and n_kv) stay.
     *
     * @param j
     * @param t
     */
    private void leaveFromDish(int j, Integer t) {
        Integer k = kJt.get(j).get(t); // k = self._k_jt[j][t]
        if (k <= 0) {                                           // assert k > 0
            throw new IllegalArgumentException(String.format("k <= 0;\tk: %d", k));
        }
        assert mK.get(k) > 0;           // assert self._m_k[k] > 0
        if (mK.get(k) <= 0) {
            throw new IllegalArgumentException(String.format("mK.get(k) <= 0;\tmK.get(k): %d", mK.get(k)));
        }

        mK.set(k, mK.get(k) - 1);       // self._m_k[k] -= 1
        m -= 1;                         // self._m -= 1
        if (mK.get(k) == 0) {           // if self._m_k[k] == 0:
            boolean removed = usingK.remove(k);  //      self._using_k.remove(k)
            assert removed;
            kJt.get(j).set(t, 0);      //      self._k_jt[j][t] = 0
        }
    }

    private void leaveFromTable(int j, int i) {
        Integer t = tJi.get(j).get(i);      // t = self.t_ji[j][i]
        if (t > 0) {                        // if t > 0:
            Integer k = kJt.get(j).get(t); //      k = self._k_jt[j][t]

            if (k <= 0) {
                throw new IllegalArgumentException("4: k <= 0");
            }

            // Decreases counters.
            Integer v = xJi.get(j).get(i);                             // v = self._x_ji[j][i]
            nKv.get(k).put(v, nKv.get(k).get(v) - 1);                   // self._n_kv[k][v] -= 1
            nK.set(k, nK.get(k) - 1.0);                                 // self._n_k[k] -= 1
            nJt.get(j).set(t, nJt.get(j).get(t) - 1);                 // self._n_jt[j][t] -= 1
            nJtv.get(j).get(t).put(v, nJtv.get(j).get(t).get(v) - 1); // self._n_jtv[j][t][v] -= 1
            if (nJt.get(j).get(t).equals(0)) {                              // if self._n_jt[j][t] == 0:
                removeTable(j, t);                                      // self._remove_table(j, t)
            }
        }
    }

    /**
     * Removes the table where all guests are gone.
     *
     * @param j
     * @param t must be integer to remove by object and not by index.
     */
    private void removeTable(int j, Integer t) {
        Integer k = kJt.get(j).get(t); //    k = self._k_jt[j][t]
        usingT.get(j).remove(t);        //    self._using_t[j].remove(t)
        mK.set(k, mK.get(k) - 1);       //    self._m_k[k] -= 1
        m -= 1;                         //    self._m -= 1

        if (mK.get(k) < 0) {            //    assert self._m_k[k] >= 0
            throw new IllegalArgumentException(String.format("mK.get(k) < 0;\tmK.get(k): %d", mK.get(k)));
        }
        if (mK.get(k) == 0) {           //    if self._m_k[k] == 0:
            // Removes topic (dish) where all tables are gone.
            usingK.remove(k);           //    self._using_k.remove(k)
        }
    }

    /**
     * Note: Python code
     * def _calc_f_k(self, v):
     * return [n_kv[v] for n_kv in self._n_kv] / self._n_k
     *
     * @param v
     * @return
     */
    private Vector<Double> calcFK(int v) {
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < nKv.size(); i++) {
            double fk = nKv.get(i).get(v) / nK.get(i);
            result.add(fk);
        }
        return new Vector<>(result);
    }

    /**
     * @param j
     * @param fk
     */
    private Vector<Double> calcTablePosterior(int j, Vector<Double> fk) {
        List<Integer> usingT = this.usingT.get(j);                                  // using_t = self._using_t[j]

        Vector<Integer> tmp1V = new Vector<>(nJt.get(j)).get(usingT);
        Vector<Integer> tmp2V = new Vector<>(kJt.get(j)).get(usingT);

        Vector<Double> tmp3Vector = fk.get(tmp2V);

        Vector<Double> pTVector = tmp1V.times(tmp3Vector);                          // p_t = self._n_jt[j][using_t] * f_k[self._k_jt[j][using_t]]
        Double pXJiAlt = new Vector<>(mK).dot(fk) + (gamma / vocabularySize);       // p_x_ji = numpy.inner(self._m_k, f_k) + self._gamma / self._V
        pTVector.set(0, pXJiAlt * alpha / (gamma + m));                             // p_t[0] = p_x_ji * self._alpha / (self._gamma + self._m)

        return pTVector.dividedBy(pTVector.sum());                                  // return p_t / p_t.sum()
    }

    /**
     * Calculates dish (topic) posterior when one word is removed.
     *
     * @param fk
     */
    private Vector<Double> calcDishPosteriorW(Vector<Double> fk) {
        Vector<Double> pK = new Vector<>(mK).times(fk).get(usingK);                                 //    p_k = (self._m_k * f_k)[self._using_k]
        pK.set(0, gamma / vocabularySize);                                                        //    p_k[0] = self._gamma / self._V
        return pK.dividedBy(pK.sum());                                                //        return p_k / p_k.sum()
    }

    /**
     * This is commonly used by sampling_t and sampling_k.
     *
     * @return
     */
    private Integer addNewDish() {
        boolean broke = false;
        int kNew;
        for (kNew = 0; kNew < usingK.size(); kNew++) {  // for k_new, k in enumerate(self._using_k):
            if (kNew != usingK.get(kNew)) {             // if k_new != k:
                broke = true;
                break;                                      // break
            }
        }

        if (!broke) {                                   // else:
            kNew = usingK.size();                       // k_new = len(self._using_k)
            if (kNew >= nKv.size()) {                       // if k_new >= len(self._n_kv):
                int newSize = kNew + 1;
                resizeDouble(nK, newSize, true);    // self._n_k = numpy.resize(self._n_k, k_new + 1) # resize makes a repeated copy!
                resizeInteger(mK, newSize, true);   // self._n_k = numpy.resize(self._n_k, k_new + 1) # resize makes a repeated copy!
                nKv.add(null);                              // self._n_kv.append(None)
            }

            if (kNew != usingK.get(usingK.size() - 1) + 1) {
                throw new IllegalArgumentException(String.format("kNew: %d;\tusingK.get(usingK.size() - 1) + 1: %d", kNew, usingK.get(usingK.size() - 1) + 1));
            }
            if (kNew >= nKv.size()) {
                throw new IllegalArgumentException(String.format("kNew: %d;\tnKv.size(): %d", kNew, nKv.size()));       //assert k_new < len(self._n_kv)
            }
        }

        usingK.add(kNew, kNew);                     //        self._using_k.insert(k_new, k_new)
        nK.set(kNew, beta * vocabularySize);        //        self._n_k[k_new] = self._beta * self._V
        mK.set(kNew, 0);                            //        self._m_k[k_new] = 0
        nKv.set(kNew, new DefaultMap<>(beta));      //        self._n_kv[k_new] = DefaultDict(self._beta)

        return kNew;                                //        return k_new
    }

    /**
     * Resizes (only increases) a list.
     *
     * @param list       list to increase.
     * @param newSize    new size.
     * @param repeatList whether the new values should be filled with a repeated copy of this list or 0.
     */
    private void resizeDouble(List<Double> list, int newSize, boolean repeatList) {
        int difference = newSize - list.size();
        for (int i = 0; i < difference; i++) {
            if (repeatList) {
                list.add(list.get(i));
            } else {
                list.add(0.0);
            }
        }
    }

    /**
     * Resizes (only increases) a list.
     *
     * @param list       list to increase.
     * @param newSize    new size.
     * @param repeatList whether the new values should be filled with a repeated copy of this list or 0.
     */
    private void resizeInteger(List<Integer> list, int newSize, boolean repeatList) {
        int difference = newSize - list.size();
        for (int i = 0; i < difference; i++) {
            if (repeatList) {
                list.add(list.get(i));
            } else {
                list.add(0);
            }
        }
    }

    /**
     * Assigns guest xJi to a new table and draw topic (dish) of the table.
     *
     * @param j
     * @param kNew
     * @return
     */
    private Integer addNewTable(int j, Integer kNew) {
        assert usingK.contains(kNew);   //    assert k_new in self._using_k
        if (!usingK.contains(kNew)) {
            throw new IllegalArgumentException("8: usingK not contains kNew");
        }
        boolean broke = false;
        int tNew;
        for (tNew = 0; tNew < usingT.get(j).size(); tNew++) {      //        for t_new, t in enumerate(self._using_t[j]):
            if (tNew != usingT.get(j).get(tNew)) {                 //            if t_new != t: break
                broke = true;
                break;
            }
        }

        if (!broke) {                                               //            else:
            tNew = usingT.get(j).size();                            //    t_new = len(self._using_t[j])
            int newSize = tNew + 1;
            resizeInteger(nJt.get(j), newSize, false); //    self._n_jt[j].resize(t_new + 1)
            resizeInteger(kJt.get(j), newSize, false); //    self._n_jt[j].resize(t_new + 1)
            nJtv.get(j).add(null);                                 //    self._n_jtv[j].append(None)

        }
        usingT.get(j).add(tNew, tNew);                          //    self._using_t[j].insert(t_new, t_new)
        nJt.get(j).set(tNew, 0);                               //    self._n_jt[j][t_new] = 0  # to make sure
        nJtv.get(j).set(tNew, new DefaultMap<>(0));//    self._n_jtv[j][t_new] = DefaultDict(0)

        kJt.get(j).set(tNew, kNew);                            //    self._k_jt[j][t_new] = k_new
        mK.set(kNew, mK.get(kNew) + 1);                         //    self._m_k[k_new] += 1
        m += 1;
        return tNew;
    }

    private void seatAtTable(int j, int i, Integer tNew) {
        if (!usingT.get(j).contains(tNew)) {
            throw new IllegalArgumentException(String.format("usingT.get(j): %s;\ttNew: %d", usingT.get(j), tNew)); //    assert t_new in self._using_t[j]
        }
        tJi.get(j).set(i, tNew);                                    //    self.t_ji[j][i] = t_new
        nJt.get(j).set(tNew, nJt.get(j).get(tNew) + 1);           //    self._n_jt[j][t_new] += 1

        Integer kNew = kJt.get(j).get(tNew);                       //    k_new = self._k_jt[j][t_new]
        nK.set(kNew, nK.get(kNew) + 1);                             //    self._n_k[k_new] += 1

        Integer v = xJi.get(j).get(i);                                     //    v = self._x_ji[j][i]
        nKv.get(kNew).put(v, nKv.get(kNew).get(v) + 1.0);                   //    self._n_kv[k_new][v] += 1
        nJtv.get(j).get(tNew).put(v, nJtv.get(j).get(tNew).get(v) + 1);   //    self._n_jtv[j][t_new][v] += 1
    }
}
