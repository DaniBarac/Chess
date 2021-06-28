package chess;

public class AIThread extends Thread {

    private ChessAI ai;
    private Move aiMove = null;
    public AIThread(ChessAI ai) {
        this.ai = ai;
    }

    @Override
    public void run() {
        aiMove = null;
        aiMove = ai.findMove();
    }

    public Move getAiMove() {
        return aiMove;
    }
}
