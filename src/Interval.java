import java.util.*;

public class Interval {

    public static final int DISTANCE_INDEX = 0;
    public static final int NOTE_INDEX = 1;
    public static final int DIRECTION_INDEX = 2;

    private static NoteNode firstNode = initializeNodes();
    private static Map<String, Integer> semitonesMap = createSemitonesMap();

    public static NoteNode initializeNodes() {
        NoteNode c = new NoteNode(null, null, "C", 2);
        NoteNode d = new NoteNode(null, null, "D", 2);
        NoteNode e = new NoteNode(null, null, "E", 1);
        NoteNode f = new NoteNode(null, null, "F", 2);
        NoteNode g = new NoteNode(null, null, "G", 2);
        NoteNode a = new NoteNode(null, null, "A", 2);
        NoteNode b = new NoteNode(null, null, "B", 1);
        connectNodes(c, d, e, f, g, a, b);
        return c;
    }

    private static Map<String, Integer> createSemitonesMap() {
        HashMap<String, Integer> result = new HashMap<>();
        result.put("m2", 1);
        result.put("M2", 2);
        result.put("m3", 3);
        result.put("M3", 4);
        result.put("P4", 5);
        result.put("P5", 7);
        result.put("m6", 8);
        result.put("M6", 9);
        result.put("m7", 10);
        result.put("M7", 11);
        result.put("P8", 12);
        return result;
    }

    private static void connectNodes(NoteNode c, NoteNode d, NoteNode e, NoteNode f,
                                     NoteNode g, NoteNode a, NoteNode b) {
        c.setNext(d);
        d.setNext(e);
        e.setNext(f);
        f.setNext(g);
        g.setNext(a);
        a.setNext(b);
        b.setNext(c);

        c.setPrev(b);
        b.setPrev(a);
        a.setPrev(g);
        g.setPrev(f);
        f.setPrev(e);
        e.setPrev(d);
        d.setPrev(c);
    }

    public static String intervalConstruction(String[] args) {

        validateArrayIsNotNull(args);
        validateArrayLength(args);

        int degree = Integer.parseInt(args[DISTANCE_INDEX].substring(1));
        String noteWithoutSemitones = removeSemitones(args[NOTE_INDEX]);
        String direction = args.length == 3 ? args[DIRECTION_INDEX] : "asc";

        NoteNode startNode = findNodeByNote(noteWithoutSemitones);
        int interval = semitonesMap.get(args[DISTANCE_INDEX]);

        return findNote(startNode, degree, direction, args[NOTE_INDEX], interval);
    }

    public static String intervalIdentification(String[] args) {

        validateArrayIsNotNull(args);
        validateArrayLength(args);

        String direction = args.length == 3 ? args[DIRECTION_INDEX] : "asc";
        NoteNode firstNode = findNodeByNote(removeSemitones(args[0]));
        NoteNode secondNode = findNodeByNote(removeSemitones(args[1]));

        long semitonesDistance = calculateDistance(firstNode, secondNode, direction);
        if (direction.equals("asc")){
            semitonesDistance = semitonesDistance + createSemitoneCounter(args[0]) + createSemitoneCounter(args[1]);
        } else {
            semitonesDistance = semitonesDistance + createSemitoneCounter(args[0]) - createSemitoneCounter(args[1]);
        }

        Set<Map.Entry<String, Integer>> entries = semitonesMap.entrySet();
        for (Map.Entry<String, Integer> entry : entries) {
            if (Math.abs(semitonesDistance) == entry.getValue()) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException();
    }

    private static int calculateDistance(NoteNode firstNode, NoteNode secondNode, String direction) {
        int distanceInSemitones = 0;
        while (firstNode != secondNode) {
            if ("asc".equals(direction)) {
                distanceInSemitones = distanceInSemitones + firstNode.getSemitonesDistance();
                firstNode = firstNode.getNext();
            } else {
                firstNode = firstNode.getPrev();
                distanceInSemitones = distanceInSemitones + firstNode.getSemitonesDistance();
            }
        }
        return distanceInSemitones;
    }

    private static void validateArrayIsNotNull(String[] args) {
        if (args == null) {
            throw new IllegalArgumentException("Input array can't be null");
        }
    }

    private static void validateArrayLength(String[] args) {
        if (args.length < 2 || args.length > 3) {
            throw new IllegalArgumentException("Illegal number of elements in input array");
        }
    }

    private static String removeSemitones(String arg) {
        return arg.replaceAll("#", "").replaceAll("b", "");
    }

    private static String findNote(NoteNode startNote, int degree, String direction, String note, int semitones) {

        long semitoneCounter = createSemitoneCounter(note);

        NoteNode result = startNote;
        for (int i = 1; i < degree; i++) {
            if ("asc".equals(direction)) {
                semitoneCounter = semitoneCounter - result.getSemitonesDistance();
                result = result.getNext();
            } else {
                result = result.getPrev();
                semitoneCounter = semitoneCounter + result.getSemitonesDistance();
            }
        }

        semitoneCounter = ("asc".equals(direction)) ?
                semitoneCounter + semitones : semitoneCounter - semitones;

        return addSemitones(result.getNote(), semitoneCounter);
    }

    private static long createSemitoneCounter(String note) {
        long sharpCount = note.chars().filter(x -> '#' == x).count();
        long flatCount = note.chars().filter(x -> 'b' == x).count();

        return sharpCount - flatCount;
    }

    private static String addSemitones(String note, long semitoneCounter) {
        StringBuilder noteBuilder = new StringBuilder(note);
        while (semitoneCounter < 0) {
            noteBuilder.append("b");
            semitoneCounter++;
        }
        while (semitoneCounter > 0) {
            noteBuilder.append("#");
            semitoneCounter--;
        }
        return noteBuilder.toString();
    }

    private static NoteNode findNodeByNote(String note) {
        NoteNode startNode = firstNode;
        NoteNode currentNode = startNode;

        while (true) {
            if (currentNode.getNote().equals(note)) {
                return currentNode;
            }
            currentNode = currentNode.getNext();
            if (currentNode.equals(startNode)) {
                throw new IllegalArgumentException("Can't find ");
            }
        }
    }

    private static class NoteNode {

        private NoteNode prev;
        private NoteNode next;
        private String note;
        private int semitonesDistance;

        public NoteNode(NoteNode prev, NoteNode next,
                        String note, int semitonesDistance) {
            this.prev = prev;
            this.next = next;
            this.note = note;
            this.semitonesDistance = semitonesDistance;
        }

        public NoteNode getPrev() {
            return prev;
        }

        public NoteNode getNext() {
            return next;
        }

        public String getNote() {
            return note;
        }

        public int getSemitonesDistance() {
            return semitonesDistance;
        }

        public void setPrev(NoteNode prev) {
            this.prev = prev;
        }

        public void setNext(NoteNode next) {
            this.next = next;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NoteNode noteNode = (NoteNode) o;
            return semitonesDistance == noteNode.semitonesDistance &&
                    Objects.equals(note, noteNode.note);
        }

        @Override
        public int hashCode() {
            return Objects.hash(note, semitonesDistance);
        }

        @Override
        public String toString() {
            return "NoteNode[ note = " + note + " ]";
        }
    }

}
