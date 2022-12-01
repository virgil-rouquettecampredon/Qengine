package qengine.program;

public class Benchmark {
    private String nameDataFile;
    private String nameQueryFolder;
    private int nbTriplets;
    private int nbQueries;
    private long timeReadingData;
    private long timeReadingQueries;
    private long timeCreatingDico;
    private int nbIndex;
    private long timeCreatingIndex;
    private long timeWorkload; //Process queries
    private long timeTotal;

    public Benchmark() {
        this.nameDataFile = "";
        this.nameQueryFolder = "";
        this.nbTriplets = 0;
        this.nbQueries = 0;
        this.timeReadingData = 0;
        this.timeReadingQueries = 0;
        this.timeCreatingDico = 0;
        this.nbIndex = 0;
        this.timeCreatingIndex = 0;
        this.timeWorkload = 0;
        this.timeTotal = 0;
    }

    public String getNameDataFile() {
        return nameDataFile;
    }

    public void setNameDataFile(String nameDataFile) {
        this.nameDataFile = nameDataFile;
    }

    public String getNameQueryFolder() {
        return nameQueryFolder;
    }

    public void setNameQueryFolder(String nameQueryFolder) {
        this.nameQueryFolder = nameQueryFolder;
    }

    public int getNbTriplets() {
        return nbTriplets;
    }

    public void setNbTriplets(int nbTriplets) {
        this.nbTriplets = nbTriplets;
    }

    public int getNbQueries() {
        return nbQueries;
    }

    public void setNbQueries(int nbQueries) {
        this.nbQueries = nbQueries;
    }

    public long getTimeReadingData() {
        return timeReadingData;
    }

    public void setTimeReadingData(long timeReadingData) {
        this.timeReadingData = timeReadingData;
    }

    public long getTimeReadingQueries() {
        return timeReadingQueries;
    }

    public void setTimeReadingQueries(long timeReadingQueries) {
        this.timeReadingQueries = timeReadingQueries;
    }

    public long getTimeCreatingDico() {
        return timeCreatingDico;
    }

    public void setTimeCreatingDico(long timeCreatingDico) {
        this.timeCreatingDico = timeCreatingDico;
    }

    public int getNbIndex() {
        return nbIndex;
    }

    public void setNbIndex(int nbIndex) {
        this.nbIndex = nbIndex;
    }

    public long getTimeCreatingIndex() {
        return timeCreatingIndex;
    }

    public void setTimeCreatingIndex(long timeCreatingIndex) {
        this.timeCreatingIndex = timeCreatingIndex;
    }

    public long getTimeWorkload() {
        return timeWorkload;
    }

    public void setTimeWorkload(long timeWorkload) {
        this.timeWorkload = timeWorkload;
    }

    public long getTimeTotal() {
        return timeTotal;
    }

    public void setTimeTotal(long timeTotal) {
        this.timeTotal = timeTotal;
    }

    @Override
    public String toString() {
        return nameDataFile + "  ,  " + nameQueryFolder + "  ,  " +  nbTriplets + "  ,  " + nbQueries + "  ,  " + timeReadingData + "  ,  " + timeReadingQueries + "  ,  " + timeCreatingDico + "  ,  " + nbIndex + "  ,  " + timeCreatingIndex + "  ,  " + timeWorkload + "  ,  " + timeTotal +  "\n";
    }
}
