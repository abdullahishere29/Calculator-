package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.calculator.databinding.ActivityMainBinding
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import java.lang.ArithmeticException

class MainActivity : AppCompatActivity() {
    var lastNumeric = false
    var stateError = false
    var lastDot = false
    var resultDisplayed = false // Flag to indicate if the result is displayed
    var lastResult = "" // Variable to store the last result
    private lateinit var expression: Expression
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    // Clear all inputs and reset states
    fun onAllClearClick(view: View) {
        binding.dataTv.text = ""
        binding.resultTv.text = ""
        stateError = false
        lastDot = false
        lastNumeric = false
        resultDisplayed = false
        lastResult = ""
        binding.resultTv.visibility = View.GONE
        binding.dataTv.visibility = View.VISIBLE
    }

    // Evaluate the expression and display the result when the equal button is pressed
    fun onEqualClick(view: View) {
        onEqual()
    }

    // Capture and append digit input to the expression
    fun onDigitClick(view: View) {
        if (stateError) {
            // If there's an error, replace the expression with the new digit
            binding.dataTv.text = (view as Button).text
            stateError = false
        } else {
            // Otherwise, append the new digit to the existing expression
            binding.dataTv.append((view as Button).text)
        }
        lastNumeric = true
        resultDisplayed = false // Reset flag when new input is entered
        binding.resultTv.visibility = View.GONE // Ensure result is hidden when new input is entered
        binding.dataTv.visibility = View.VISIBLE // Ensure input data is visible
    }

    // Append operator input to the expression
    fun onOperatorClick(view: View) {
        if (resultDisplayed) {
            // If result is displayed, use it as the starting point for new operation
            binding.dataTv.text = lastResult + (view as Button).text
            resultDisplayed = false
        } else if (!stateError && lastNumeric) {
            binding.dataTv.append((view as Button).text)
        }
        lastDot = false
        lastNumeric = false
        binding.resultTv.visibility = View.GONE // Ensure result is hidden when operator is entered
        binding.dataTv.visibility = View.VISIBLE // Ensure input data is visible
    }

    // Remove the last character from the expression
    fun onBackClick(view: View) {
        if (!resultDisplayed) { // Prevent backspace if the result is displayed
            val length = binding.dataTv.text.length
            if (length > 0) {
                binding.dataTv.text = binding.dataTv.text.substring(0, length - 1)
                // Update states
                stateError = false
                lastNumeric = binding.dataTv.text.isNotEmpty() && binding.dataTv.text.last().isDigit()
                lastDot = binding.dataTv.text.contains(".")
            }
            binding.resultTv.visibility = View.GONE // Ensure result is hidden when backspace is pressed
            binding.dataTv.visibility = View.VISIBLE // Ensure input data is visible
        }
    }

    // Clear the current expression
    fun onClearClick(view: View) {
        binding.dataTv.text = ""
        lastNumeric = false
        resultDisplayed = false // Reset flag when clear is pressed
        binding.resultTv.visibility = View.GONE // Ensure result is hidden when clear is pressed
        binding.dataTv.visibility = View.VISIBLE // Ensure input data is visible
    }

    // Evaluate the current expression and display the result
    fun onEqual() {
        if (lastNumeric && !stateError) {
            val txt = binding.dataTv.text.toString()
            expression = ExpressionBuilder(txt).build()

            try {
                val result = expression.evaluate()
                // Check if the result is an integer
                if (result == result.toLong().toDouble()) {
                    binding.resultTv.text = "=" + result.toLong().toString()
                    lastResult = result.toLong().toString()
                } else {
                    binding.resultTv.text = "=" + result.toString()
                    lastResult = result.toString()
                }
                binding.resultTv.visibility = View.VISIBLE // Show the result
                binding.dataTv.visibility = View.GONE // Hide the input data
                resultDisplayed = true // Set flag when result is displayed
            } catch (ex: ArithmeticException) {
                Log.e("evaluate error", ex.toString())
                binding.resultTv.visibility = View.VISIBLE
                binding.resultTv.text = "= Error"
                binding.dataTv.visibility = View.GONE // Hide the input data
                stateError = true
                lastNumeric = false
                resultDisplayed = false
            }
        }
    }
}
