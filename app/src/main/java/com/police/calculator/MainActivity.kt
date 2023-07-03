package com.police.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.police.calculator.databinding.ActivityMainBinding
import java.util.Stack

/**
 * study review
 * Themes : Status bar transport, Button Style 따로 저장하여 중복 제거
 * strings.xml 로 string 빼기
 * ConstraintLayout
 * Spannable -> setSpan() 함수에서 수행
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // String 더하기 연산을 위한 StringBuilder
    private val sb = StringBuilder()

    // 연산자 text color 변경을 위해 연산자 Index 저장
    private val spannableIdxList = Stack<Int>()

    // 여는 괄호 개수
    private var countOpenBracket = 0

    // 현재 연산자
    private var operator = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initButton()
    }

    private fun initButton() {
        // 숫자 버튼 클릭 이벤트 - 계산식에 숫자 반영
        binding.zeroButton.setOnClickListener { clickNumber(getString(R.string.zero)) }
        binding.oneButton.setOnClickListener { clickNumber(getString(R.string.one)) }
        binding.twoButton.setOnClickListener { clickNumber(getString(R.string.two)) }
        binding.threeButton.setOnClickListener { clickNumber(getString(R.string.three)) }
        binding.fourButton.setOnClickListener { clickNumber(getString(R.string.four)) }
        binding.fiveButton.setOnClickListener { clickNumber(getString(R.string.five)) }
        binding.sixButton.setOnClickListener { clickNumber(getString(R.string.six)) }
        binding.sevenButton.setOnClickListener { clickNumber(getString(R.string.seven)) }
        binding.eightButton.setOnClickListener { clickNumber(getString(R.string.eight)) }
        binding.nineButton.setOnClickListener { clickNumber(getString(R.string.nine)) }

        // 지우기 버튼 클릭 이벤트 - 계산식 맨 뒤 문자 지우기
        binding.eraseButton.setOnClickListener {
            if (sb.isNotEmpty()) {
                // 마지막 문자가 연산자일 경우 span list 에서 idx 삭제
                if (checkEndTextIsOperator()) {
                    spannableIdxList.pop()
                }

                // 전체 문자에서 마지막 부분 삭제
                sb.deleteCharAt(sb.length - 1)
                binding.resultEditText.text = setSpan()
            }
        }

        // 'C' 버튼 클릭 이벤트 - 리셋
        binding.resetButton.setOnClickListener {
            sb.setLength(0)
            binding.resultEditText.setText(sb.toString())
            countOpenBracket = 0
            spannableIdxList.clear()
        }

        // 연산자 버튼 클릭 이벤트 - 계산식에 연산자 반영
        binding.plusButton.setOnClickListener { clickOperator(getString(R.string.plus)) }
        binding.minusButton.setOnClickListener { clickOperator(getString(R.string.minus)) }
        binding.multiplyButton.setOnClickListener { clickOperator(getString(R.string.multiply)) }
        binding.divideButton.setOnClickListener { clickOperator(getString(R.string.divide)) }
        binding.moduloButton.setOnClickListener { clickOperator(getString(R.string.modulo)) }

        // 괄호 버튼 클릭 이벤트
        binding.bracketButton.setOnClickListener {
            if (sb.toString() == ""
                || checkEndTextIsOperator()
                || sb[sb.length - 1].toString() == getString(R.string.open_bracket)
            ) {
                // 마지막이 연산자 혹은 여는 괄호('(') 일 경우,
                // 비어있는 경우 여는 괄호('(') 추가
                clickNumber(getString(R.string.open_bracket))
                countOpenBracket += 1
            } else if (countOpenBracket > 0) {
                // 마지막이 숫자이고 앞서 여는 괄호가 있었을 경우 닫는 괄호(')') 추가
                clickNumber(getString(R.string.close_bracket))
                countOpenBracket -= 1
            } else {
                // 하이라이트 색 idx 추가
                spannableIdxList.add(sb.length)

                // 마지막이 숫자이고 앞서 여는 괄호가 없었을 경우 곱하기, 여는 괄호('x(') 추가
                clickNumber(getString(R.string.multiply) + getString(R.string.open_bracket))

                countOpenBracket += 1
            }
        }

        // 등호 버튼 클릭 이벤트 - 계산
        binding.equalButton.setOnClickListener {
            val numbers = sb.toString().split(operator)

            if (numbers.size < 2 || numbers[0] == "" || numbers[1] == "") {
                // 수식이 불완전할 때
                Toast.makeText(this, getString(R.string.incomplete), Toast.LENGTH_SHORT).show()
            } else if (!numbers[0].isDigitsOnly() || !numbers[1].isDigitsOnly() || numbers.size > 2) {
                // 수식의 숫자가 3개 이상일 때
                Toast.makeText(this, getString(R.string.plz_two), Toast.LENGTH_SHORT).show()
            } else {
                // 수식의 숫자가 2개일 때, 연산 수행
                var result = 0
                when (operator) {
                    getString(R.string.plus) -> result = numbers[0].toInt() + numbers[1].toInt()
                    getString(R.string.minus) -> result = numbers[0].toInt() - numbers[1].toInt()
                    getString(R.string.multiply) -> result = numbers[0].toInt() * numbers[1].toInt()
                    getString(R.string.divide) -> result = numbers[0].toInt() / numbers[1].toInt()
                    getString(R.string.modulo) -> result = numbers[0].toInt() % numbers[1].toInt()
                }

                sb.setLength(0)
                sb.append(result)
                spannableIdxList.clear()
                countOpenBracket = 0
                binding.resultEditText.setText(sb.toString())
            }
        }
    }

    // 이미 입력 되어 있는 문자가 '0'이라면 기존 '0' 지우기
    private fun checkTextStartZero() {
        if (sb.toString() == getString(R.string.zero)) {
            sb.setLength(0)
        }
    }

    // 수식의 마지막 부분이 연산자인지 return
    private fun checkEndTextIsOperator(): Boolean {
        return (sb[sb.length - 1].toString() == getString(R.string.plus)
                || sb[sb.length - 1].toString() == getString(R.string.minus)
                || sb[sb.length - 1].toString() == getString(R.string.divide)
                || sb[sb.length - 1].toString() == getString(R.string.multiply)
                || sb[sb.length - 1].toString() == getString(R.string.modulo))
    }

    // 연산자 IDX 에 색 반영
    private fun setSpan(): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder(sb.toString())

        for (i in 0 until spannableIdxList.size) {
            spannableStringBuilder.setSpan(
                ForegroundColorSpan(getColor(R.color.yellow)),
                spannableIdxList[i],
                spannableIdxList[i] + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spannableStringBuilder
    }

    // 숫자 버튼 클릭 시 화면에 반영
    private fun clickNumber(number: String) {
        // 시작이 0일 경우 예외 처리
        checkTextStartZero()
        sb.append(number)
        binding.resultEditText.text = setSpan()
    }

    // 연산자 버튼 클릭 시 화면에 반영
    private fun clickOperator(operator: String) {
        this.operator = operator

        if (sb.toString() != "") {
            // 마지막 문자가 연산자일 경우 기존 마지막 연산자 삭제
            if (checkEndTextIsOperator()) {
                sb.deleteCharAt(sb.length - 1)
            }
            sb.append(operator)

            // 하이라이트 색 idx 추가
            spannableIdxList.add(sb.length - 1)

            binding.resultEditText.text = setSpan()
        } else {
            Toast.makeText(this, getString(R.string.incomplete), Toast.LENGTH_SHORT).show()
        }
    }
}