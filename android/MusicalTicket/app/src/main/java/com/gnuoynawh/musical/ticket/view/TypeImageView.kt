package com.gnuoynawh.musical.ticket.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap

class TypeImageView: AppCompatImageView {

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    /**
     * 그리기 타입
     */
    enum class DrawType {
        VERTICAL,
        HORIZONTAL,
        DIAGONAL
    }

    var drawType: DrawType = DrawType.VERTICAL

    /**
     * 그리기
     */
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        val drawable = drawable ?: return
        if (width == 0 || height == 0)
            return

        val bm = drawable.toBitmap().copy(Bitmap.Config.ARGB_8888, true)
        val bitmap = Bitmap.createScaledBitmap(bm, width, height, false)
        val outputBitmap = makeBitmap(bitmap)

        if (outputBitmap != null) {
            canvas?.drawBitmap(outputBitmap, 0f, 0f, null)
        }
    }

    /**
     * 변형된 비트맵 생성
     */
    private fun makeBitmap(bitmap: Bitmap): Bitmap? {

        // 기본설정
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val rect = Rect(0, 0, bitmap.width, bitmap.height)

        val paint = Paint()
        paint.color = Color.WHITE

        val path = when (drawType) {
            DrawType.DIAGONAL -> makeTriangle(bitmap)
            DrawType.VERTICAL -> makeVertical(bitmap)
            DrawType.HORIZONTAL -> makeHorizontal(bitmap)
        }

        // 그리기
        canvas.drawARGB(0,0,0,0)
        canvas.drawPath(path, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }

    /**
     * 세로 그리기
     */
    private fun makeVertical(bitmap: Bitmap): Path {
        /*
                point1   point4
               (w/2, 0)  (w, 0)
                      ---
                      | |
                      | |
                      | |
                      | |
                      ---
                point2   point3
               (w/2, h)  (w, h)
         */

        // 꼭지점을 잡아준다
        val point1 = Point(bitmap.width / 2, 0)
        val point2 = Point(bitmap.width / 2, bitmap.height)
        val point3 = Point(bitmap.width, bitmap.height)
        val point4 = Point(bitmap.width, 0)

        // 꼭지점을 연결하는 line 을 그려준다
        val path = Path()
        path.moveTo(point1.x.toFloat(), point1.y.toFloat())
        path.lineTo(point2.x.toFloat(), point2.y.toFloat())
        path.lineTo(point3.x.toFloat(), point3.y.toFloat())
        path.lineTo(point4.x.toFloat(), point4.y.toFloat())
        path.lineTo(point1.x.toFloat(), point1.y.toFloat())
        path.close()

        return path
    }

    /**
     * 가로 그리기
     */
    private fun makeHorizontal(bitmap: Bitmap): Path {
        /*
              point1    point4
             (0, h/2)  (w, h/2)
                   ------
                   |    |
                   |    |
                   ------
              point2   point3
              (0, h)   (w, h)
         */

        // 꼭지점을 잡아준다
        val point1 = Point(0, bitmap.height / 2)
        val point2 = Point(0, bitmap.height)
        val point3 = Point(bitmap.width, bitmap.height)
        val point4 = Point(bitmap.width, bitmap.height / 2)

        // 꼭지점을 연결하는 line 을 그려준다
        val path = Path()
        path.moveTo(point1.x.toFloat(), point1.y.toFloat())
        path.lineTo(point2.x.toFloat(), point2.y.toFloat())
        path.lineTo(point3.x.toFloat(), point3.y.toFloat())
        path.lineTo(point4.x.toFloat(), point4.y.toFloat())
        path.lineTo(point1.x.toFloat(), point1.y.toFloat())
        path.close()

        return path
    }

    /**
     * 삼각형 만들기
     */
    private fun makeTriangle(bitmap: Bitmap): Path {
        /*
                      point1
                      (w, 0)
                       /|
                      / |
                     /  |
                    /   |
                   ------
              point2   point3
              (0, h)   (w, h)
         */

        // 꼭지점을 잡아준다
        val point1 = Point(bitmap.width, 0)
        val point2 = Point(0, bitmap.height)
        val point3 = Point(bitmap.width, bitmap.height)

        // 꼭지점을 연결하는 line 을 그려준다
        val path = Path()
        path.moveTo(point1.x.toFloat(), point1.y.toFloat())
        path.lineTo(point2.x.toFloat(), point2.y.toFloat())
        path.lineTo(point3.x.toFloat(), point3.y.toFloat())
        path.lineTo(point1.x.toFloat(), point1.y.toFloat())
        path.close()

        return path
    }
}