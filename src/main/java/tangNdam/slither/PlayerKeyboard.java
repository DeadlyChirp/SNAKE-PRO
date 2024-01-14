//package tangNdam.slither;
//
//import java.awt.event.KeyEvent;
//import java.util.ArrayDeque;
//import java.util.Deque;
//
//public class PlayerKeyboard extends Player {
//    private boolean upPressed = false;
//    private boolean leftPressed = false;
//    private boolean rightPressed = false;
//
//    PlayerKeyboard(String name) {
//        super(name);
//    }
//
//    @Override
//    public Wish action(SlitherModel model) {
//        double angle = model.snake.getWantedAngle();
//        boolean boost = false;
//
//        if (leftPressed) {
//            angle -= 0.1; // Adjust as necessary
//        }
//        if (rightPressed) {
//            angle += 0.1; // Adjust as necessary
//        }
//        if (upPressed) {
//            boost = true;
//        }
//
//        return new Wish(angle, boost);
//    }
//
//    public void keyPressed(int keyCode) {
//        switch (keyCode) {
//            case KeyEvent.VK_UP:
//            case KeyEvent.VK_W:
//                upPressed = true;
//                break;
//            case KeyEvent.VK_LEFT:
//            case KeyEvent.VK_A:
//                leftPressed = true;
//                break;
//            case KeyEvent.VK_RIGHT:
//            case KeyEvent.VK_D:
//                rightPressed = true;
//                break;
//        }
//    }
//
//    public void keyReleased(int keyCode) {
//        switch (keyCode) {
//            case KeyEvent.VK_UP:
//            case KeyEvent.VK_W:
//                upPressed = false;
//                break;
//            case KeyEvent.VK_LEFT:
//            case KeyEvent.VK_A:
//                leftPressed = false;
//                break;
//            case KeyEvent.VK_RIGHT:
//            case KeyEvent.VK_D:
//                rightPressed = false;
//                break;
//        }
//    }
//}
