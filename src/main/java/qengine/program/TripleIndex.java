package qengine.program;

public abstract class TripleIndex {
    private int firstObjectIndex;
    private int secondObjectIndex;
    private int thirdObjectIndex;

    public TripleIndex(int firstObjectIndex, int secondObjectIndex, int thirdObjectIndex) {
        this.firstObjectIndex = firstObjectIndex;
        this.secondObjectIndex = secondObjectIndex;
        this.thirdObjectIndex = thirdObjectIndex;
    }

    public int getFirstObjectIndex() {
        return firstObjectIndex;
    }

    public int getSecondObjectIndex() {
        return secondObjectIndex;
    }

    public int getThirdObjectIndex() {
        return thirdObjectIndex;
    }

    public String toString(){
        return "TripleIndex{" + "firstObjectIndex=" + firstObjectIndex + ", secondObjectIndex=" + secondObjectIndex + ", thirdObjectIndex=" + thirdObjectIndex + '}';
    }
}
