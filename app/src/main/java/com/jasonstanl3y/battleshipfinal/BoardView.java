package com.jasonstanl3y.battleshipfinal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BoardView extends AppCompatImageView {

    Paint paint;
    public static int cellWidth;

    public BoardView(Context context, AttributeSet attrs) {

        super(context, attrs);

        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        paint.setTypeface(typeface);
        paint.setTextAlign(Paint.Align.CENTER);


    }

    @Override
    protected void onDraw(Canvas canvas) {

        int height = canvas.getHeight() - 1;
        int width = canvas.getWidth() - 1;
        if (height > width) {
            cellWidth = width / 11;
        } else {
            cellWidth = height / 11;
        }

        //  int cellHeight = height / 12;
        //        } else {
        //Draw a box around the entire image element with lines

        paint.setTextSize(cellWidth);
//        canvas.drawLine(0, 0, width, 0, paint);
//        canvas.drawLine(0, 0, 0, height, paint);
//        canvas.drawLine(0, height, width, height, paint);
//        canvas.drawLine(width, 0, width, height, paint);

        //Draw the Grid

        for (int i = 0; i < 12; i++) {
            //Horizontal
            canvas.drawLine(0, (cellWidth * i), cellWidth * 11, (cellWidth * i), paint);
            //Vertical
            canvas.drawLine((cellWidth * i), 0, (cellWidth * i), cellWidth * 11, paint);
        }

        //Assign points to the double array of gamecell objects

        for (int y = 0; y < 11; y++) {
            for (int x = 0; x < 11; x++) {
                BoardSetup.attackingGrid[x][y].setTopLeft(new Point(x * cellWidth, y * cellWidth));
                BoardSetup.attackingGrid[x][y].setBottomRight(new Point((x + 1) * cellWidth, (y + 1) * cellWidth));
            }
        }


        Rect textBounds = new Rect();
        paint.getTextBounds("A", 0, 1, textBounds);

        String[] letterArray = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        String[] numberArray = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        int textHeight = textBounds.height();
        int textWidth = textBounds.width();
        int textX;
        int textY;

        textX = cellWidth / 2;
        textY = cellWidth + ((cellWidth / 2) - (textHeight));
        textY += cellWidth;


        for (String letter : letterArray) {
            canvas.drawText(letter, textX, textY, paint);
            textY += cellWidth;
        }

        textX = cellWidth / 2;
        textY = cellWidth + ((cellWidth / 2) - (textHeight));
        textX += cellWidth;

        for (String number : numberArray) {
            canvas.drawText(number, textX, textY, paint);
            textX += cellWidth;
        }
        //Walk through the double array of gamecells and draw whatever is needed in each cell


        float w = paint.measureText("W", 0, 0);
        float center = (cellWidth / 2); // - ((w / 2));
        for (int y = 0; y < 11; y++) {
            for (int x = 0; x < 11; x++) {
                if (BoardSetup.attackingGrid[x][y].getHit()) {
                    //Draw * in cell
                    drawCell("X", BoardSetup.attackingGrid, x, y, center, textHeight, canvas);
                } else if (BoardSetup.attackingGrid[x][y].getHasShip()) {
                    // Draw a S in the cell

                    drawCell("S", BoardSetup.attackingGrid, x, y, center, textHeight, canvas);

                } else if (BoardSetup.attackingGrid[x][y].getMiss()) {
                    // Draw an - in the cell
                    drawCell("-", BoardSetup.attackingGrid, x, y, center, textHeight, canvas);

                } else if (BoardSetup.attackingGrid[x][y].getWaiting()) {
                    // Draw a W in the cell

                    drawCell("W", BoardSetup.attackingGrid, x, y, center, textHeight, canvas);

                }

            }
        }

    }

    void drawCell(String contents, GameCell[][] grid, int x, int y, float center, int textHeight, Canvas canvas) {
        canvas.drawText(contents, grid[x][y].getTopLeft().x + center,
                grid[x][y].getBottomRight().y -
                        ((grid[x][y].getBottomRight().y - grid[x][y].getTopLeft().y) - textHeight) / 2, paint);
    }
}
