package domainmodel;


public final class EnhancerRegion implements Comparable<EnhancerRegion>{
    private final String chromosomeName;
    private final int startPosition;
    private final int endPosition;
    private final String ID;
    private final float score;

    public static EnhancerRegion fromText(final String text) {
        final String[] columns =  text.split("\t");
        if (columns.length != 5) {
            return null;
        }
        try {
        return new EnhancerRegion(columns[0],
                Integer.parseInt(columns[1]),
                Integer.parseInt(columns[2]),
                columns[3],
                Float.parseFloat(columns[4]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public EnhancerRegion(String chromosomeName, int startPosition, int endPosition, String ID, float score) {
        this.chromosomeName = chromosomeName;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.ID = ID;
        this.score = score;
    }

    public String getChromosomeName() {
        return chromosomeName;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public String getID() {
        return ID;
    }

    public float getScore() {
        return score;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(chromosomeName);
        builder.append('\t');
        builder.append(startPosition);
        builder.append('\t');
        builder.append(endPosition);
        builder.append('\t');
        builder.append(ID);
        builder.append('\t');
        builder.append(score);
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnhancerRegion that = (EnhancerRegion) o;

        if (endPosition != that.endPosition) return false;
        if (Float.compare(that.score, score) != 0) return false;
        if (startPosition != that.startPosition) return false;
        if (!ID.equals(that.ID)) return false;
        if (!chromosomeName.equals(that.chromosomeName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = chromosomeName.hashCode();
        result = 31 * result + startPosition;
        result = 31 * result + endPosition;
        result = 31 * result + ID.hashCode();
        result = 31 * result + (score != +0.0f ? Float.floatToIntBits(score) : 0);
        return result;
    }

    @Override
    public int compareTo(EnhancerRegion other) {
        int r = getChromosomeName().compareTo(other.getChromosomeName());
        if (r != 0) return r;
        r = new Integer(getStartPosition()).compareTo(other.getStartPosition());
        if (r != 0) return r;
        r = new Integer(getEndPosition()).compareTo(other.getEndPosition());
        if (r != 0) return r;
        r = getID().compareTo(other.getID());
        if (r != 0) return r;
        return new Float(score).compareTo(other.getScore());
    }
}
