package com.example.meda;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FloatingViewService extends Service {
    private WindowManager windowManager;
    private View floatingView;
    private DrawView drawView;
    private View colorPickerView;
    private boolean isFloatingViewVisible = false;
    private WindowManager.LayoutParams colorPickerParams;


    @Override
    public IBinder onBind(Intent intent) {
        return null; // Binding not used
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //  메뉴바 레이아웃 추가
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_view_layout, null);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.END;
        params.x = 0; // Offset from the right edge
        params.y = 0; // Offset from the top
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(floatingView, params);
        LinearLayout menubar = floatingView.findViewById(R.id.menubar);
        ImageButton writeButton = floatingView.findViewById(R.id.write_button);
        ImageButton pauseButton = floatingView.findViewById(R.id.pause_button);
        ImageButton stopButton = floatingView.findViewById(R.id.stop_button);
        menubar.setVisibility(View.VISIBLE);
        //---여기까지 메뉴바 설정 ------

        drawView = new DrawView(this);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFloatingViewVisible) {
                    //컬러피커 초기화 전
                    if (colorPickerView == null) {
                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        //컬러피커
                        colorPickerView = inflater.inflate(R.layout.color_and_size_picker_layout, null);
                        menubar.post(new Runnable() {
                            @Override
                            public void run() {
                                int[] location = new int[2];
                                menubar.getLocationOnScreen(location);
                                //드로우뷰
                                int menubarLeftX = location[0];
                                // 드로우뷰의 너비를 메뉴바의 왼쪽 x좌표까지로 설정
                                WindowManager.LayoutParams drawViewParams = new WindowManager.LayoutParams(
                                        menubarLeftX - 150, // 너비
                                        WindowManager.LayoutParams.MATCH_PARENT,
                                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                                        PixelFormat.TRANSLUCENT);
                                if (drawView.getParent() != null) {
                                    windowManager.updateViewLayout(drawView, drawViewParams);
                                } else {
                                    windowManager.addView(drawView, drawViewParams);
                                }

                                //컬러피커
                                int buttonWidth = menubar.getWidth();
                                // 컬러피커 위치시킴
                                colorPickerParams = new WindowManager.LayoutParams(
                                        WindowManager.LayoutParams.WRAP_CONTENT,
                                        WindowManager.LayoutParams.WRAP_CONTENT,
                                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                        PixelFormat.TRANSLUCENT);
                                colorPickerParams.gravity = Gravity.TOP | Gravity.START;
                                // 왼쪽에 위치시킴
                                colorPickerParams.x = location[0] - buttonWidth;
                                colorPickerParams.y = location[1];
                                // 컬러피커 추가
                                windowManager.addView(colorPickerView, colorPickerParams);
                            }
                        });

//                    // drawView를 WindowManager에 추가
//                    WindowManager.LayoutParams drawViewParams = new WindowManager.LayoutParams(
//                            WindowManager.LayoutParams.MATCH_PARENT,
//                            WindowManager.LayoutParams.MATCH_PARENT,
//                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
//                            PixelFormat.TRANSLUCENT);
//                    drawView.startDrawingMode();
//                    windowManager.addView(drawView, drawViewParams);

                        // 컬러피커의 너비 및 위치 계산
//                        colorPickerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                            @Override
//                            public void onGlobalLayout() {
//                                // 리스너가 더 이상 필요 없으면 제거
//                                colorPickerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//
//                                int colorPickerWidth = colorPickerView.getWidth();
//                                int colorPickerRightX = colorPickerParams.x + colorPickerWidth - 150;
//
//                                // 드로우뷰 너비 조정
//                                WindowManager.LayoutParams drawViewParams = new WindowManager.LayoutParams(
//                                        colorPickerRightX, // 너비 조정
//                                        WindowManager.LayoutParams.MATCH_PARENT,
//                                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
//                                        PixelFormat.TRANSLUCENT);
//                                // 드로우뷰 추가
//                                windowManager.addView(drawView, drawViewParams);
//                            }
//                        });

                        //버튼 선택
                        Button colorButtonBlack = colorPickerView.findViewById(R.id.color_button_black);
                        colorButtonBlack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // DrawView의 펜 색상을 검정색으로 설정
                                drawView.setPaintColor("#000000");

                                drawView.startDrawingMode();
                            }
                        });
                        Button colorButtonRed = colorPickerView.findViewById(R.id.color_button_red);
                        colorButtonRed.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                drawView.setPaintColor("#FFCDD2");

                                // 그림 그리기 모드로 전환합니다.
                                drawView.startDrawingMode();
                            }
                        });
                        Button colorButtonBlue = colorPickerView.findViewById(R.id.color_button_blue);
                        colorButtonBlue.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                drawView.setPaintColor("#3D5AFE");
                                // DrawView를 화면에 추가

                                drawView.startDrawingMode();
                            }
                        });
                        Button colorButtonGreen = colorPickerView.findViewById(R.id.color_button_green);
                        colorButtonGreen.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                drawView.setErase(false);
                                drawView.setPaintColor("#E0E0E0");

                                drawView.startDrawingMode();
                            }
                        });
                        Button colorButtonPurple = colorPickerView.findViewById(R.id.color_button_purple);
                        colorButtonPurple.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                drawView.setErase(false);
                                drawView.setPaintColor("#FF6C007E");

                                //  drawView.startDrawingMode();
                            }
                        });
                        ImageButton btn_erase = colorPickerView.findViewById(R.id.btn_erase);
                        btn_erase.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                drawView.setErase(true);
                            }
                        });
                    }
                    else {
                        // 이미 초기화된 colorPickerView를 다시 보여줌
                        colorPickerView.setVisibility(View.VISIBLE);
                        drawView.setVisibility(View.VISIBLE);
                    }                        // Visible 상태를 true로 업데이트합니다.
                    isFloatingViewVisible = true;

                } else {  // 두 번째 클릭: colorPickerView를 숨기고 drawView의 캔버스 클리어

                    // WindowManager에서 colorPickerView를 제거
                    if (colorPickerView != null) {
                        colorPickerView.setVisibility(View.GONE);
                        drawView.clearCanvas();
                        drawView.setVisibility(View.GONE);

                    }
                    // drawView의 캔버스 클리어

                    isFloatingViewVisible = false;
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 브로드캐스트 보내기
                Intent intent = new Intent("ACTION_PAUSE_RECORDING");
                sendBroadcast(intent);


            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                        if (colorPickerView != null) {
//                            windowManager.removeView(colorPickerView);
//                            colorPickerView = null;
//                        }
//                        // drawView의 캔버스 클리어
//                        drawView.clearCanvas();
//                        drawView.setVisibility(View.GONE);
//                        isFloatingViewVisible = false;
//                        stopSelf(); // Stop the service
                Intent intent = new Intent("ACTION_STOP_RECORDING");
                sendBroadcast(intent);


            }
        });

    }


    @Override

    public void onDestroy() {
//        if (drawView != null && drawView.getParent() != null) {
//            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//            windowManager.removeView(drawView);
//        }
        if (floatingView != null && floatingView.getParent() != null) {
            windowManager.removeView(floatingView); // FloatingView 제거
        }
        // 다른 정리 작업 수행
        super.onDestroy();
    }
}
