package patterns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import research.ResearchPaper;

//Когда исследователь публ статью,подписчики получают увед. Компоненты: ResearchJournal- Субъект (Наблюдаемый объект),JournalSubscriber — Интерфейс наблюдателя

//Сания

//штука, чтобы подписчики могли быть сохранены и восстановлены вместе с журналом 
interface JournalSubscriber {
    void onNewPublication(ResearchPaper paper, String journalName);
}

//конкретный наблюдатель, который получает уведомления о новых публикациях
class SubscriberUser implements JournalSubscriber, Serializable {
    private String name;
    public SubscriberUser(String name) { this.name = name; }

    @Override
    public void onNewPublication(ResearchPaper paper, String journalName) {
        System.out.printf("  [NOTIFICATION] %s: New paper in '%s': \"%s\"%n",
                name, journalName, paper.getTitle());
    }
    public String getName() { return name; }
}

// предмет публикации, который уведомляет подписчиков
public class ResearchJournal implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private List<JournalSubscriber> subscribers = new ArrayList<>();
    private List<ResearchPaper> publishedPapers = new ArrayList<>();

    public ResearchJournal(String name) { this.name = name; }

    public void subscribe(String subscriberName) {
        subscribers.add(new SubscriberUser(subscriberName));
        System.out.println(subscriberName + " subscribed to " + name);
    }

    public void publish(ResearchPaper paper) {
        publishedPapers.add(paper);
        System.out.println("Published: " + paper.getTitle() + " in " + name);
        notifySubscribers(paper);
    }

    private void notifySubscribers(ResearchPaper paper) {
        subscribers.forEach(s -> s.onNewPublication(paper, name));
    }

    public List<ResearchPaper> getPublishedPapers() {
         return publishedPapers; 
        }
    public String getName() {
         return name; 
        }
    public int subscriberCount() { 
        return subscribers.size();
     }
}

