package research;

import employees.Teacher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
public class ResearchTeacher extends Teacher implements Researcher {

    private static final long serialVersionUID = 1L;

    private List<ResearchPaper> papers = new ArrayList<>();

    public ResearchTeacher(String login, String password, String firstName, String lastName) {
        super(login, password, firstName, lastName);
    }

    // ── Researcher interface ───────────────────────────────
    @Override
    public List<ResearchPaper> getPapers() { return papers; }

    @Override
    public void addPaper(ResearchPaper paper) { papers.add(paper); }

    // ── Extended Menu ──────────────────────────────────────
    @Override
    public void showMenu() {
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== RESEARCHER-TEACHER MENU — " + getFullName() + " ===");
            System.out.println("1. Teacher options");
            System.out.println("2. View my papers (sorted)");
            System.out.println("3. Add a paper");
            System.out.println("4. Show h-index");
            System.out.println("5. Get citation for a paper");
            System.out.println("0. Logout");
            System.out.print("Choice: ");

            switch (sc.nextLine().trim()) {
                case "1":
                    super.showMenu();
                    return;
                case "2":
                    System.out.println("Sort by: 1=Citations  2=Year  3=Pages");
                    String sort = sc.nextLine().trim();
                    Comparator<ResearchPaper> cmp = switch (sort) {
                        case "2" -> Researcher.byYear();
                        case "3" -> Researcher.byPages();
                        default  -> Researcher.byCitations();
                    };
                    printPapers(cmp);
                    break;
                case "3":
                    System.out.print("Title: ");   String title = sc.nextLine().trim();
                    System.out.print("Year: ");    int year  = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Citations: ");int cites = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Pages: ");   int pages = Integer.parseInt(sc.nextLine().trim());
                    addPaper(new ResearchPaper(title, year, cites, pages, this));
                    System.out.println("Paper added.");
                    break;
                case "4":
                    System.out.println("h-index: " + calculateHIndex());
                    break;
                case "5":
                    if (papers.isEmpty()) { System.out.println("No papers."); break; }
                    papers.forEach(p -> System.out.println("  " + p));
                    System.out.print("Enter paper title: ");
                    String ptitle = sc.nextLine().trim();
                    papers.stream().filter(p -> p.getTitle().equalsIgnoreCase(ptitle)).findFirst()
                            .ifPresentOrElse(p -> {
                                System.out.println("\n--- PLAIN ---\n" + p.getCitation(ResearchPaper.Format.PLAIN));
                                System.out.println("\n--- BIBTEX ---\n" + p.getCitation(ResearchPaper.Format.BIBTEX));
                            }, () -> System.out.println("Paper not found."));
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
