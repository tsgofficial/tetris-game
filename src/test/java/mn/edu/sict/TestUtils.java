package mn.edu.sict;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TestUtils {

    public static Tetrominoes[][] getPrivateBoard(Object target) {
        try {
            Field f = target.getClass().getDeclaredField("board");
            f.setAccessible(true);
            return (Tetrominoes[][]) f.get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean invokeTryMove(Object target, int dx, int dy) {
        try {
            Method m = target.getClass().getDeclaredMethod("tryMove", int.class, int.class);
            m.setAccessible(true);
            return (boolean) m.invoke(target, dx, dy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void invokePieceDropped(Object target) {
        try {
            Method m = target.getClass().getDeclaredMethod("pieceDropped");
            m.setAccessible(true);
            m.invoke(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void invokeClearBoard(Object target) {
        try {
            Method m = target.getClass().getDeclaredMethod("clearBoard");
            m.setAccessible(true);
            m.invoke(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void invokeCheckFullRows(Object target) throws Exception {
        Method m = target.getClass().getDeclaredMethod("checkFullRows");
        m.setAccessible(true);
        m.invoke(target);
    }

    public static void invokeUpdateGhostPiece(Object target) throws Exception {
        Method m = target.getClass().getDeclaredMethod("updateGhostPiece");
        m.setAccessible(true);
        m.invoke(target);
    }

    /**
     * Sets a private field in an object using reflection.
     * 
     * @param object    The object to modify
     * @param fieldName The name of the private field
     * @param value     The value to set
     * @throws Exception If reflection fails
     */
    public static void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    /**
     * Gets the value of a private field in an object using reflection.
     * 
     * @param object    The object to access
     * @param fieldName The name of the private field
     * @return The value of the field
     * @throws Exception If reflection fails
     */
    public static Object getPrivateField(Object object, String fieldName) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

    /**
     * Invokes the tryMove method on the Tetris instance.
     * 
     * @param tetris The Tetris instance
     * @param dx     Horizontal movement
     * @param dy     Vertical movement
     * @return Result of the tryMove method
     * @throws Exception If reflection fails
     */
    public static boolean invokeTryMove(Tetris tetris, int dx, int dy) throws Exception {
        Method method = tetris.getClass().getDeclaredMethod("tryMove", int.class, int.class);
        method.setAccessible(true);
        return (boolean) method.invoke(tetris, dx, dy);
    }

    /**
     * Gets the board from the Tetris instance.
     * 
     * @param tetris The Tetris instance
     * @return The game board
     * @throws Exception If reflection fails
     */
    public static Tetrominoes[][] getPrivateBoard(Tetris tetris) throws Exception {
        Field field = tetris.getClass().getDeclaredField("board");
        field.setAccessible(true);
        return (Tetrominoes[][]) field.get(tetris);
    }

    /**
     * Invokes the checkFullRows method on the Tetris instance.
     * 
     * @param tetris The Tetris instance
     * @throws Exception If reflection fails
     */
    public static void invokeCheckFullRows(Tetris tetris) throws Exception {
        Method method = tetris.getClass().getDeclaredMethod("checkFullRows");
        method.setAccessible(true);
        method.invoke(tetris);
    }

    /**
     * Invokes the pieceDropped method on the Tetris instance.
     * 
     * @param tetris The Tetris instance
     * @throws Exception If reflection fails
     */
    public static void invokePieceDropped(Tetris tetris) throws Exception {
        Method method = tetris.getClass().getDeclaredMethod("pieceDropped");
        method.setAccessible(true);
        method.invoke(tetris);
    }

    /**
     * Invokes the updateGhostPiece method on the Tetris instance.
     * 
     * @param tetris The Tetris instance
     * @throws Exception If reflection fails
     */
    public static void invokeUpdateGhostPiece(Tetris tetris) throws Exception {
        Method method = tetris.getClass().getDeclaredMethod("updateGhostPiece");
        method.setAccessible(true);
        method.invoke(tetris);
    }

    /**
     * Invokes the oneLineDown method on the Tetris instance.
     * 
     * @param tetris The Tetris instance
     * @throws Exception If reflection fails
     */
    public static void invokeOneLineDown(Tetris tetris) throws Exception {
        Method method = tetris.getClass().getDeclaredMethod("oneLineDown");
        method.setAccessible(true);
        method.invoke(tetris);
    }

    /**
     * Invokes the loadBestScore method on the Tetris instance.
     * 
     * @param tetris The Tetris instance
     * @throws Exception If reflection fails
     */
    public static void invokeLoadBestScore(Tetris tetris) throws Exception {
        Method method = tetris.getClass().getDeclaredMethod("loadBestScore");
        method.setAccessible(true);
        method.invoke(tetris);
    }

    /**
     * Invokes the saveBestScore method on the Tetris instance.
     * 
     * @param tetris The Tetris instance
     * @throws Exception If reflection fails
     */
    public static void invokeSaveBestScore(Tetris tetris) throws Exception {
        Method method = tetris.getClass().getDeclaredMethod("saveBestScore");
        method.setAccessible(true);
        method.invoke(tetris);
    }

    /**
     * Invokes the newPiece method on the Tetris instance.
     * 
     * @param tetris The Tetris instance
     * @throws Exception If reflection fails
     */
    public static void invokeNewPiece(Tetris tetris) throws Exception {
        Method method = tetris.getClass().getDeclaredMethod("newPiece");
        method.setAccessible(true);
        method.invoke(tetris);
    }
}
