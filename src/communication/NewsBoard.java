package communication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewsBoard implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<News> newsList = new ArrayList<>();

    public void post(News news) {
        newsList.add(0, news); // newest first
    }

    public void postTopCitedResearcher(String name, int hIndex) {
        post(new News(
                "🏆 Top Cited Researcher of the Year",
                name + " is the most cited researcher with h-index = " + hIndex + "!",
                "System"
        ));
    }

    public List<News> getAll() { return Collections.unmodifiableList(newsList); }

    public void printAll() {
        if (newsList.isEmpty()) {
            System.out.println("  No news available.");
        } else {
            newsList.forEach(n -> System.out.println(n + "\n"));
        }
    }
}
