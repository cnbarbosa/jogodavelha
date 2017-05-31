package cnbarbosa.apps.jogodavelha;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;
import java.util.*;

/**
 * @author cnbarbosa
 */

public class GameActivity extends AppCompatActivity {

    public int JOGADOR = 1;
    public int ANDROID = 2;
    Random random;
    private RunGame runGame;
    private Button mTabuleiroBtns[][];
    private TextView mInfoTextView;
    private TextView mPontosJogador1;
    private TextView mPontosEmpate;
    private TextView mPontosJogador2;
    private TextView mJogadorUmTexto;
    private TextView mJogadorDoisTexto;
    private int mJogadorUmContador = 0;
    private int mEmpateContador = 0;
    private int mJogadorDoisContador = 0;
    private Button mNovoJogo, mSairJogo;
    private int Primeiro = 0;
    private int Contador = 0;
    private boolean ehFimJogo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        mTabuleiroBtns = new Button[3][3];
        mTabuleiroBtns[0][0] = (Button) findViewById(R.id.um);
        mTabuleiroBtns[0][1] = (Button) findViewById(R.id.dois);
        mTabuleiroBtns[0][2] = (Button) findViewById(R.id.tres);
        mTabuleiroBtns[1][0] = (Button) findViewById(R.id.quatro);
        mTabuleiroBtns[1][1] = (Button) findViewById(R.id.cinco);
        mTabuleiroBtns[1][2] = (Button) findViewById(R.id.seis);
        mTabuleiroBtns[2][0] = (Button) findViewById(R.id.sete);
        mTabuleiroBtns[2][1] = (Button) findViewById(R.id.oito);
        mTabuleiroBtns[2][2] = (Button) findViewById(R.id.nove);

        mNovoJogo = (Button) findViewById(R.id.novoJogo);
        mSairJogo = (Button) findViewById(R.id.sairJogo);
        // Compo de texto
        mInfoTextView = (TextView) findViewById(R.id.informacao);
        mPontosJogador1 = (TextView) findViewById(R.id.pontosJogador);
        mPontosEmpate = (TextView) findViewById(R.id.pontosEmpate);
        mPontosJogador2 = (TextView) findViewById(R.id.pontosAndroid);
        mJogadorUmTexto = (TextView) findViewById(R.id.jogador);
        mJogadorDoisTexto = (TextView) findViewById(R.id.android);
        // Contador
        mPontosJogador1.setText(Integer.toString(mJogadorUmContador));
        mPontosEmpate.setText(Integer.toString(mEmpateContador));
        mPontosJogador2.setText(Integer.toString(mJogadorDoisContador));

        random = new Random();

        mSairJogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameActivity.this.finish();
            }
        });

        final CharSequence[] items = {"Android", "Jogador"};

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Quem vai primeiro?");
        alertDialog.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item] == "Android") {
                    Primeiro = 1;
                }
                else if (items[item] == "Jogador") {
                    Primeiro = 2;
                }
                dialog.dismiss();

                runGame = new RunGame(GameActivity.this);

                if (Primeiro == 1) {
                    iniciarNovoJogo(true); // Android
                }
                if (Primeiro == 2) {
                    iniciarNovoJogo(false); // Jogador
                }

            }
        });
        alertDialog.show();

        mNovoJogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Contador % 2 == 0) {
                    iniciarNovoJogo(false);
                    Contador++;
                } else {
                    iniciarNovoJogo(true);
                    Contador++;
                }
            }
        });

    }

    private void iniciarNovoJogo(boolean vaiPrimeiro) {
        mLimparTabuleiro();

        mJogadorUmTexto.setText("Jogador:");
        mJogadorDoisTexto.setText("Android:");

        if (vaiPrimeiro) {
            // Android Primeiro
            mInfoTextView.setText("Vez Android.");
            setMover(random.nextInt(3), random.nextInt(3), ANDROID);
            vaiPrimeiro = false;
        } else {
            // Jogador Primeiro
            mInfoTextView.setText("Vez Jogador.");
            vaiPrimeiro = true;
        }
        ehFimJogo = false;
    }

    private void mLimparTabuleiro() {
        runGame.resetTabuleiro();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                mTabuleiroBtns[i][j].setText("");
                mTabuleiroBtns[i][j].setEnabled(true);
                mTabuleiroBtns[i][j].setOnClickListener(new ButtonClickListener(i, j));
                mTabuleiroBtns[i][j].setBackgroundResource(R.drawable.empty);
            }
        }
    }

    public void setMover(int x, int y, int player) {
        runGame.aMover(x, y, player);
        mTabuleiroBtns[x][y].setEnabled(false);
        if (player == 1) {
            mTabuleiroBtns[x][y].setBackgroundResource(R.drawable.x);
        } else {
            mTabuleiroBtns[x][y].setBackgroundResource(R.drawable.o);
        }
    }

    private class ButtonClickListener implements View.OnClickListener {
        int x,y;

        public ButtonClickListener(int i, int j) {
            this.x = i;
            this.y = j;
        }

        @Override
        public void onClick(View v) {
            if (!ehFimJogo) { //
                if (mTabuleiroBtns[x][y].isEnabled()) {
                    setMover(x, y, JOGADOR);
                    int vencedor = runGame.estadoJogo();

                    if (vencedor == runGame.JOGANDO) {
                        mInfoTextView.setText(R.string.vez_jogador);
                        int[] result = runGame.mover();
                        setMover(result[0], result[1], ANDROID);
                        vencedor = runGame.estadoJogo();
                    }

                    vencedor = runGame.estadoJogo();

                    if (vencedor == runGame.JOGANDO) {
                        mInfoTextView.setText(R.string.vez_jogador);
                    } else if (vencedor == runGame.DESENHAR) { // se desenhar
                        mInfoTextView.setText(R.string.resultado_empate);
                        mEmpateContador++;
                        mPontosEmpate.setText(Integer.toString(mEmpateContador));
                        ehFimJogo = true;
                    } else if (vencedor == runGame.GANHOU_CRUZ) { // X veceu
                        mInfoTextView.setText(R.string.resultado_jogador);
                        mJogadorUmContador++;
                        mPontosJogador1.setText(Integer.toString(mJogadorUmContador));
                        ehFimJogo = true;
                    } else if (vencedor == runGame.GANHOU_NADA) { // O venceu
                        mInfoTextView.setText(R.string.resultado_android);
                        mJogadorDoisContador++;
                        mPontosJogador2.setText(Integer.toString(mJogadorDoisContador));
                        ehFimJogo = true;
                    }
                }
            }
        }
    }
}
