package com.example.calcinfixprefix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var stack = mutableListOf<Char>()
    private var equeu = mutableListOf<Char>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_calc.setOnClickListener {
            val expression = et_expression.text.toString()
            tw_answer.text = ""
            stack.clear()
            equeu.clear()
            getPostFix(expression)
            var postFixEx = buildString {
                equeu.forEach {
                    append(it.toString())
                }
            }
            tw_answer.text = postFixEx
        }


    }

    private fun getPostFix(expression: String) {
        expression.forEach {
            when {
                it == '(' -> stack.add(it)
                it == ')' -> {
                    if (expression.contains('(')) {
                        popStack()
                    }
                }
                Regex("[\\d]").containsMatchIn(it.toString()) -> equeu.add(it)
                Regex("[+-]").containsMatchIn(it.toString()) -> {
                    if (stack.isEmpty() || stack.last() == '(') stack.add(it)
                    else {
                        if (Regex("[/*]").containsMatchIn(it.toString())) {
                            popStack()
                            stack.add(it)
                        } else {
                            equeu.add(stack.last())
                            stack[stack.lastIndex] = it
                        }
                    }
                }
                Regex("[/*]").containsMatchIn(it.toString()) -> {
                    if (stack.isNotEmpty() && (stack.last() == '*' || stack.last() == '/')) {
                        popStack()
                    }
                    stack.add(it)
                }

            }
        }
        if (stack.isNotEmpty()) {
            for (i in stack.lastIndex downTo 0) {
                if (stack[i] != '(') {
                    equeu.add(stack[i])
                } else throw Exception("Error")
            }
        }
    }

    private fun popStack() {
        Loop@ for (i in stack.lastIndex downTo 0) {
            if (stack[i] == '(') {
                stack[i] = ' '
                break@Loop
            }
            equeu.add(stack[i])
        }
        stack.removeIf { it == ' ' }
    }

}
