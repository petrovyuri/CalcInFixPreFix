package com.example.calcinfixprefix

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mStack = mutableListOf<String>()
    private var mQueue = mutableListOf<String>()
    private var mExpressionList = mutableListOf<String>()
    private lateinit var mTvAnswer:TextView
    private lateinit var mTvPostfix:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mTvAnswer = tv_answer
        mTvPostfix = tv_postfix
        btn_calc.setOnClickListener {
            val expression = et_expression.text.toString()
            parseExpression(expression)
            getPostFixEx()
            calcPostFix()
        }
    }

    private fun parseExpression(expression:String) {
        expression.forEach {
           mExpressionList.add(it.toString())
        }
    }

    private fun getPostFixEx() {
        // Перебираем expressionList
        mExpressionList.forEach {
            when {
                //Если входящий элемент левая скобка делаем PUSH
                it == "(" -> push(it)

                //Если входящий элемент правая скобка делаем POP
                it == ")" -> {
                    if (mExpressionList.contains("(")) {
                        pop()
                    }
                }

                //Если входящий элемент число, то добавляем в очередь
                Regex("[\\d]").containsMatchIn(it) -> addQueue(it)

                //Если входящий элемент + или -
                Regex("[+-]").containsMatchIn(it) ->
                    /* Проверяем, если стек пустой или на вершине стека левая скобка,
                    * то делаем PUSH */
                    if (mStack.isEmpty() || mStack.last() == "(") push(it)
                    /* Иначе, если на вершине стека оператор имеющий больший
                    * приоритет, то делаем POP, потом PUSH */
                    else if (mStack.last().contains(Regex("[/*]"))) {
                        pop()
                        push(it)
                    }
                    // Иначе просто делаем PUSH
                    else {
                        addQueue(mStack.last())
                        mStack[mStack.lastIndex] = it
                    }

                //Если входящий элемент * или /
                Regex("[*/]").containsMatchIn(it) -> {
                    /* Проверяем, если на вершине стека элемент с таким же приоритетом,
                    * то делаем POP */
                    if (mStack.isNotEmpty() && (mStack.last() == "*" || mStack.last() == "/")) {
                        pop()
                    }
                    // Потом делаем PUSH
                    push(it)
                }
            }
        }
        // Когда перебрали все элементы выражения, то добавляем из стека элементы в очередь
        if (mStack.isNotEmpty()) {
            for (i in mStack.lastIndex downTo 0) {
                if (mStack[i] != "(") {
                    addQueue(mStack[i])
                }
            }
        }
        var postfix = buildString {
            mQueue.forEach {
                append(it)
            }
        }
        mTvPostfix.text = postfix
    }

    private fun pop() {
        // Выгружаем стек в очередь пока не найдем левую скобку, потом удаляем скобку из стека
        Loop@ for (i in mStack.lastIndex downTo 0) {
            if (mStack[i] == "(") {
                mStack[i] = " "
                break@Loop
            }
            addQueue(mStack[i])
            mStack[i] = " "
        }
        mStack.removeIf { it == " " }
    }

    private fun addQueue(item: String) {
        mQueue.add(item)
    }

    private fun push(item: String) {
        mStack.add(item)
    }

    private fun calcPostFix() {
        //Создаем стек для работы
        val stack = mutableListOf<Int>()
        // Перебераем все эелементы очереди
        for (item in mQueue) {
            when {
                // Если входящий элемент - число, то добавляем в стек
                Regex("[\\d]").containsMatchIn(item) -> {
                    stack.add(item.toInt())
                }
                /* Если входящий элемент + , берем два последних элемента
                и производим советующую операцию */
                item == "+" -> {
                    stack[stack.lastIndex - 1] = stack[stack.lastIndex - 1] + stack.last()
                    stack.removeAt(stack.lastIndex)
                }
                /* Если входящий элемент * , берем два последних элемента
                и производим советующую операцию */
                item == "*" -> {
                    stack[stack.lastIndex - 1] = stack[stack.lastIndex - 1] * stack.last()
                    stack.removeAt(stack.lastIndex)
                }
                /* Если входящий элемент / , берем два последних элемента
                и производим советующую операцию */
                item == "/" -> {
                    stack[stack.lastIndex - 1] = stack[stack.lastIndex - 1] / stack.last()
                    stack.removeAt(stack.lastIndex)
                }
                /* Если входящий элемент -, берем два последних элемента
                 и производим советующую операцию */
                item == "-" -> {
                    stack[stack.lastIndex - 1] = stack[stack.lastIndex - 1] - stack.last()
                    stack.removeAt(stack.lastIndex)
                }
            }
        }
        mTvAnswer.text = stack.first().toString()
    }

}
