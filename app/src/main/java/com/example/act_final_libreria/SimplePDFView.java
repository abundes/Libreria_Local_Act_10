package com.example.act_final_libreria;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SimplePDFView extends AppCompatActivity implements View.OnTouchListener {

    public static final String EXTRA_PDF_PATH = "pdf_path";
    private ImageView imageView;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private float startX, startY, offsetX, offsetY;
    private float scaleFactor = 1.0f;
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 2.0f;
    private ScaleGestureDetector scaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_pdf_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Visualizador de PDF básico");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        imageView = findViewById(R.id.imageViewBack);

        ImageButton btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(view -> resetImageToDefault());

        ImageButton btnZoomIn = findViewById(R.id.btnZoomIn);
        btnZoomIn.setOnClickListener(view -> zoomIn());

        ImageButton btnZoomOut = findViewById(R.id.btnZoomOut);
        btnZoomOut.setOnClickListener(view -> zoomOut());

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        imageView.setOnTouchListener(this);

        String pdfPath = getIntent().getStringExtra(EXTRA_PDF_PATH);
        openPdf(pdfPath);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        imageView.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    startY = event.getY();
                    break;

                case MotionEvent.ACTION_UP:
                    float endX = event.getX();
                    float deltaX = endX - startX;

                    if (scaleFactor <= 1.0f && Math.abs(deltaX) > 100) {
                        if (deltaX > 0) {
                            showPreviousPage();
                        } else {
                            showNextPage();
                        }
                        resetImagePosition();
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (scaleFactor > 1.0f) {
                        float currentX = event.getX();
                        float currentY = event.getY();

                        offsetX += currentX - startX;
                        offsetY += currentY - startY;

                        float adjustedOffsetX = offsetX / scaleFactor;
                        float adjustedOffsetY = offsetY / scaleFactor;

                        imageView.setTranslationX(adjustedOffsetX);
                        imageView.setTranslationY(adjustedOffsetY);

                        startX = currentX;
                        startY = currentY;
                    }
                    break;
            }

            return true;
        });
    }
    private void resetImageToDefault() {
        scaleFactor = 1.0f;
        applyZoom(); // Método que ya tenías para aplicar el zoom
        resetImagePosition(); // Restablecer la posición
    }
    private void resetImagePosition() {
        imageView.animate()
                .translationX(0)
                .translationY(0)
                .setDuration(300)
                .start();
    }
    private void openPdf(String pdfPath) {
        try {
            File file = createFileFromAsset(pdfPath);
            if (file != null) {
                ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                pdfRenderer = new PdfRenderer(fileDescriptor);
                showPage(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private File createFileFromAsset(String assetPath) {
        String fileName = assetPath.substring(assetPath.lastIndexOf("/") + 1);
        try {
            InputStream inputStream = getAssets().open(fileName);
            File outFile = new File(getFilesDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();
            return outFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void showPage(int pageIndex) {
        if (currentPage != null) {
            currentPage.close();
        }

        currentPage = pdfRenderer.openPage(pageIndex);
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        imageView.setImageBitmap(bitmap);
    }
    @Override
    protected void onPause() {
        super.onPause();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        scaleGestureDetector.onTouchEvent(motionEvent);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = motionEvent.getX();
                startY = motionEvent.getY();
                break;

            case MotionEvent.ACTION_UP:
                float endY = motionEvent.getY();
                if (startY - endY > 100) {
                    showNextPage();
                } else if (endY - startY > 100) {
                    showPreviousPage();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                offsetX = motionEvent.getX() - startX;
                offsetY = motionEvent.getY() - startY;
                imageView.setTranslationX(offsetX);
                imageView.setTranslationY(offsetY);
                break;
        }
        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(MIN_SCALE, Math.min(scaleFactor, MAX_SCALE));
            imageView.setScaleX(scaleFactor);
            imageView.setScaleY(scaleFactor);
            return true;
        }
    }
    private void showNextPage() {
        if (currentPage.getIndex() < pdfRenderer.getPageCount() - 1) {
            showPage(currentPage.getIndex() + 1);
        }
    }
    private void showPreviousPage() {
        if (currentPage.getIndex() > 0) {
            showPage(currentPage.getIndex() - 1);
        }
    }
    private void zoomIn() {
        if (scaleFactor < MAX_SCALE) {
            scaleFactor += 0.3f;
            applyZoom();
        } else {
            Toast.makeText(this, "Ya has alcanzado el nivel máximo de zoom", Toast.LENGTH_SHORT).show();
        }
    }
    private void zoomOut() {
        if (scaleFactor > MIN_SCALE) {
            scaleFactor -= 0.1f;
            applyZoom();
        } else {
            Toast.makeText(this, "Ya has alcanzado el nivel mínimo de zoom", Toast.LENGTH_SHORT).show();
        }
    }

    private void applyZoom() {
        imageView.setScaleX(scaleFactor);
        imageView.setScaleY(scaleFactor);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (currentPage != null) {
            currentPage.close();
        }
        if (pdfRenderer != null) {
            pdfRenderer.close();
        }
        super.onDestroy();
    }

}