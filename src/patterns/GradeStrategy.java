package patterns;
import users.Mark;
public interface GradeStrategy {
    String evaluate(Mark mark);
    String getStrategyName();
}

class LetterGradeStrategy implements GradeStrategy {
    @Override
    public String evaluate(Mark mark) {
        return String.format("Total: %.1f → Grade: %s (%s)",
                mark.getTotal(), mark.getLetterGrade(),
                mark.isPassed() ? "PASSED" : "FAILED");
    }
    @Override public String getStrategyName() { 
        return "Letter Grade (A/B/C/D/F)"; 
    }
}

class GPAStrategy implements GradeStrategy {
    @Override
    public String evaluate(Mark mark) {
        double t = mark.getTotal();
        double gpa = t >= 90 ? 4.0 : t >= 80 ? 3.0 : t >= 70 ? 2.0 : t >= 60 ? 1.0 : 0.0;
        return String.format("Total: %.1f → GPA: %.1f / 4.0", t, gpa);
    }
    @Override public String getStrategyName() { return "GPA 4.0 Scale"; }
}

class PassFailStrategy implements GradeStrategy {
    @Override
    public String evaluate(Mark mark) {
        return "Result: " + (mark.isPassed() ? "✓ PASS" : "✗ FAIL")
                + " (score: " + String.format("%.1f", mark.getTotal()) + ")";
    }
    @Override public String getStrategyName() {
         return "Pass / Fail";
         }
}

class GradeEvaluator {
    private GradeStrategy strategy;

    public GradeEvaluator(GradeStrategy strategy) {
         this.strategy = strategy;
         }
    public void setStrategy(GradeStrategy strategy) {
         this.strategy = strategy;
         }
    public String evaluate(Mark mark) { 
        return strategy.evaluate(mark); 
    }
    public String currentStrategy()  { 
        return strategy.getStrategyName(); 
    }
    public static GradeEvaluator letterGrade() { 
        return new GradeEvaluator(new LetterGradeStrategy()); 
    }
    public static GradeEvaluator gpa() {
         return new GradeEvaluator(new GPAStrategy());
         }
    public static GradeEvaluator passFail(){
         return new GradeEvaluator(new PassFailStrategy());
         }
}

