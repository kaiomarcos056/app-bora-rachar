package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var editTextQuantidadePessoas: EditText
    private lateinit var editTextValorTotal: EditText
    private lateinit var textViewValorDividido: TextView
    private lateinit var tts: TextToSpeech
    private lateinit var falarValor: Button
    private lateinit var compartilhar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextQuantidadePessoas = findViewById(R.id.qtdPessoas)

        editTextValorTotal = findViewById(R.id.valorTotal)

        textViewValorDividido = findViewById(R.id.valorDividido)

        editTextQuantidadePessoas.addTextChangedListener(textWatcher)
        editTextValorTotal.addTextChangedListener(textWatcher)

        falarValor = findViewById(R.id.falarValor)

        falarValor.setOnClickListener {
            tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
                if (it == TextToSpeech.SUCCESS){
                    tts.setLanguage(Locale("pt-br", "BR"))
                    tts.setSpeechRate(1.0f)
                    tts.speak(textViewValorDividido.text.toString(), TextToSpeech.QUEUE_ADD, null)
                }
            })
        }

        compartilhar = findViewById(R.id.compartilhar)
        compartilhar.setOnClickListener {
            var qtdPessoas = editTextQuantidadePessoas.text.toString()
            var vlrTot = editTextValorTotal.text.toString()
            var vlrDiv = "0,00"

            if (qtdPessoas.isNotEmpty() && vlrTot.isNotEmpty()) {
                val quantidadePessoas = qtdPessoas.toInt()
                val valorTotal = vlrTot.toDouble()

                val valorDividido = valorTotal / quantidadePessoas
                vlrDiv = String.format("%.2f", valorDividido)

                var message = "========== BORA RACHAR ==========\n\n"
                message += "Qtd. Pessoas: ${qtdPessoas} \n\n"
                message += "Vlr. Total: R$: ${vlrTot} \n\n"
                message += "---------------------------------\n\n"
                message += "Total por pessoa:  R$ $vlrDiv \n\n"
                message += "=================================\n\n"

                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, message)
                    type = "text/plain"
                }

                startActivity(Intent.createChooser(shareIntent, "Compartilhar via"))
            }
            else{
                Toast.makeText(this, "Preencha os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        if (tts.isSpeaking) { tts.stop() }
        tts.shutdown()
        super.onDestroy()
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            calcularValorDividido()
        }
    }

    private fun calcularValorDividido() {
        val quantidadePessoasStr = editTextQuantidadePessoas.text.toString()
        val valorTotalStr = editTextValorTotal.text.toString()

        if (quantidadePessoasStr.isNotEmpty() && valorTotalStr.isNotEmpty()) {
            val quantidadePessoas = quantidadePessoasStr.toInt()
            val valorTotal = valorTotalStr.toDouble()

            val valorDividido = valorTotal / quantidadePessoas
            val valorDivididoFormatado = String.format("%.2f", valorDividido)

            textViewValorDividido.setText("R$ $valorDivididoFormatado")
        } else {
            textViewValorDividido.setText("R$ 0,00")
        }
    }
}