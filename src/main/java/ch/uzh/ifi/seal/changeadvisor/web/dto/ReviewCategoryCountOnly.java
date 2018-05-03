package ch.uzh.ifi.seal.changeadvisor.web.dto;

public class ReviewCategoryCountOnly implements ReviewCategoryReport {

    private final int reviewCount;

    private final String category;

    public ReviewCategoryCountOnly(int reviewCount, String category) {
        this.reviewCount = reviewCount;
        this.category = category;
    }

    @Override
    public int getReviewCount() {
        return reviewCount;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "ReviewCategoryCountOnly{" +
                "reviewCount=" + reviewCount +
                ", category='" + category + '\'' +
                '}';
    }
}
