package cnbarbosa.apps.jogodavelha;

import java.util.*;

/**
 * @author cnbarbosa
 */

public class RunGame {

    public final int VAZIO = 0;
    public final int CRUZ = 1;
    public final int ZERO = 2;

    public final int JOGANDO = 0;
    public final int GANHOU_CRUZ = 1;
    public final int GANHOU_NADA = 2;
    public final int DESENHAR = 3;

    public static final int LINHAS = 3, COLUNAS = 3;
    public static int[][] tabuleiro = new int[LINHAS][COLUNAS];
    public static int atualEstado;
    public static int atualJogador;
    public static int atualLinha, atualColuna;

    GameActivity gameActivity;

    public RunGame(GameActivity gameActivity) {
        this.gameActivity = gameActivity;
    }

    public void resetTabuleiro() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                tabuleiro[i][j] = 0;
            }
        }
    }

    public int[] mover() {
        int[] result = minimax(2, ZERO);
        return new int[]{result[1], result[2]};
    }

    public int[] minimax(int depth, int jogador) {
        List<int[]> proxMovimento = movimentos();

        int melhorPonto = (jogador == ZERO) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int pontoCorrente;
        int melhorLin = -1;
        int melhorCol = -1;

        if (proxMovimento.isEmpty() || depth == 0) {
            melhorPonto = avaliar();
        } else {
            for (int[] move : proxMovimento) {
                tabuleiro[move[0]][move[1]] = jogador;
                if (jogador == ZERO) {
                    pontoCorrente = minimax(depth - 1, CRUZ)[0];
                    if (pontoCorrente > melhorPonto) {
                        melhorPonto = pontoCorrente;
                        melhorLin = move[0];
                        melhorCol = move[1];
                    }
                } else {
                    pontoCorrente = minimax(depth - 1, ZERO)[0];
                    if (pontoCorrente < melhorPonto) {
                        melhorPonto = pontoCorrente;
                        melhorLin = move[0];
                        melhorCol = move[1];
                    }
                }
                // Undo mover
                tabuleiro[move[0]][move[1]] = VAZIO;
            }
        }
        return new int[]{melhorPonto, melhorLin, melhorCol};
    }

    private int avaliar() {
        int ponto = 0;
        ponto += avaliarLinha(0, 0, 0, 1, 0, 2);  // linha 0
        ponto += avaliarLinha(1, 0, 1, 1, 1, 2);  // linha 1
        ponto += avaliarLinha(2, 0, 2, 1, 2, 2);  // linha 2
        ponto += avaliarLinha(0, 0, 1, 0, 2, 0);  // linha 0
        ponto += avaliarLinha(0, 1, 1, 1, 2, 1);  // coluna 1
        ponto += avaliarLinha(0, 2, 1, 2, 2, 2);  // coluna 2
        ponto += avaliarLinha(0, 0, 1, 1, 2, 2);  // diagonal
        ponto += avaliarLinha(0, 2, 1, 1, 2, 0);  // alt diagonal
        return ponto;
    }

    private int avaliarLinha(int lin1, int col1, int lin2, int col2, int lin3, int col3) {
        int ponto = 0;

        // primeira celula
        if (tabuleiro[lin1][col1] == ZERO) {
            ponto = 1;
        } else if (tabuleiro[lin1][col1] == CRUZ) {
            ponto = -1;
        }

        // Segunda celula
        if (tabuleiro[lin2][col2] == ZERO) {
            if (ponto == 1) {
                ponto = 10;
            } else if (ponto == -1) {
                return 0;
            } else {
                ponto = 1;
            }
        } else if (tabuleiro[lin2][col2] == CRUZ) {
            if (ponto == -1) {
                ponto = -10;
            } else if (ponto == 1) {
                return 0;
            } else {
                ponto = -1;
            }
        }

        // Terceira celula
        if (tabuleiro[lin3][col3] == ZERO) {
            if (ponto > 0) {
                ponto *= 10;
            } else if (ponto < 0) {
                return 0;
            } else {
                ponto = 1;
            }
        } else if (tabuleiro[lin3][col3] == CRUZ) {
            if (ponto < 0) {
                ponto *= 10;
            } else if (ponto > 1) {
                return 0;
            } else {
                ponto = -1;
            }
        }
        return ponto;
    }

    private List<int[]> movimentos() {
        List<int[]> proxMovimento = new ArrayList<int[]>();

        int estado = estadoJogo();
        if (estado == 1 || // X
                estado == 2 || // O
                estado == 3)   // Desenho
        {
            return proxMovimento; // retorno vazio
        }

        for (int lin = 0; lin < LINHAS; ++lin) {
            for (int col = 0; col < COLUNAS; ++col) {
                if (tabuleiro[lin][col] == VAZIO) {
                    proxMovimento.add(new int[]{lin, col});
                }
            }
        }
        return proxMovimento;
    }

    public void aMover(int x, int y, int jogador) {
        tabuleiro[x][y] = jogador;
    }

    public int estadoJogo() {
        /*
         0 - Jogador
		 1 - X
		 2 - O
		 3 - Desenho
		 */

        for (int i = 0; i < LINHAS; i++) {
            if (tabuleiro[i][0] == CRUZ &&
                    tabuleiro[i][1] == CRUZ &&
                    tabuleiro[i][2] == CRUZ) {
                return GANHOU_CRUZ;
            }
            if (tabuleiro[i][0] == ZERO &&
                    tabuleiro[i][1] == ZERO &&
                    tabuleiro[i][2] == ZERO) {
                return GANHOU_NADA;
            }
        }

        for (int i = 0; i < COLUNAS; i++) {
            if (tabuleiro[0][i] == CRUZ &&
                    tabuleiro[1][i] == CRUZ &&
                    tabuleiro[2][i] == CRUZ) {
                return GANHOU_CRUZ;
            }
            if (tabuleiro[0][i] == ZERO &&
                    tabuleiro[1][i] == ZERO &&
                    tabuleiro[2][i] == ZERO) {
                return GANHOU_NADA;
            }
        }

        if (tabuleiro[0][0] == CRUZ &&
                tabuleiro[1][1] == CRUZ &&
                tabuleiro[2][2] == CRUZ) {
            return GANHOU_CRUZ;
        }
        if (tabuleiro[0][0] == ZERO &&
                tabuleiro[1][1] == ZERO &&
                tabuleiro[2][2] == ZERO) {
            return GANHOU_NADA;
        }


        if (tabuleiro[0][2] == CRUZ &&
                tabuleiro[1][1] == CRUZ &&
                tabuleiro[2][0] == CRUZ) {
            return GANHOU_CRUZ;
        }
        if (tabuleiro[0][2] == ZERO &&
                tabuleiro[1][1] == ZERO &&
                tabuleiro[2][0] == ZERO) {
            return GANHOU_NADA;
        }

        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                if (tabuleiro[i][j] != CRUZ && tabuleiro[i][j] != ZERO) {
                    return JOGANDO;
                }
            }
        }

        return DESENHAR;
    }

}
