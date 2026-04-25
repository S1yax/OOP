package users;
import java.io.Serializable;
public class Mark implements Serializable {

    private static final long serialVersionUID = 1L;

    private double attestation1; // max 30
    private double attestation2; // max 30
    private double finalExam;    // max 40

    public Mark() {}

    public Mark(double a1, double a2, double fin) {
        this.attestation1 = clamp(a1, 0, 30);
        this.attestation2 = clamp(a2, 0, 30);
        this.finalExam    = clamp(fin, 0, 40);
    }

    public void setAttestation1(double v) { attestation1 = clamp(v, 0, 30); }
    public void setAttestation2(double v) { attestation2 = clamp(v, 0, 30); }
    public void setFinalExam(double v)    { finalExam    = clamp(v, 0, 40); }

    public double getAttestation1() { return attestation1; }
    public double getAttestation2() { return attestation2; }
    public double getFinalExam()    { return finalExam; }

    public double getTotal() { return attestation1 + attestation2 + finalExam; }

    public String getLetterGrade() {
        double t = getTotal();
        if (t >= 90) return "A";
        if (t >= 80) return "B";
        if (t >= 70) return "C";
        if (t >= 60) return "D";
        return "F";
    }

    public boolean isPassed() { return getTotal() >= 50; }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    @Override
    public String toString() {
        return String.format("A1=%.1f | A2=%.1f | Final=%.1f | Total=%.1f (%s)",
                attestation1, attestation2, finalExam, getTotal(), getLetterGrade());
    }
}