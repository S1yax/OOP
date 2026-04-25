package research;
import java.util.Comparator;
import java.util.List;
public interface Researcher {
    List<ResearchPaper> getPapers();
    void addPaper(ResearchPaper paper);
    default void printPapers(Comparator<ResearchPaper> comparator) {
        List<ResearchPaper> sorted = new java.util.ArrayList<>(getPapers());
        sorted.sort(comparator);
        if (sorted.isEmpty()) {
            System.out.println("No papers published.");
        } else {
            sorted.forEach(p -> System.out.println("  " + p));
        }
    }

    default int calculateHIndex() {
        List<Integer> cites = getPapers().stream()
                .map(ResearchPaper::getCitations)
                .sorted(Comparator.reverseOrder())
                .collect(java.util.stream.Collectors.toList());
        int h = 0;
        for (int i = 0; i < cites.size(); i++) {
            if (cites.get(i) >= i + 1) h = i + 1;
            else break;
        }
        return h;
    }

    static Comparator<ResearchPaper> byCitations() {
        return Comparator.comparingInt(ResearchPaper::getCitations).reversed();
    }
    static Comparator<ResearchPaper> byYear() {
        return Comparator.comparingInt(ResearchPaper::getYear).reversed();
    }
    static Comparator<ResearchPaper> byPages() {
        return Comparator.comparingInt(ResearchPaper::getPages).reversed();
    }
}
