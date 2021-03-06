package com.example.kickboardguard;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Bundle;



import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.getSystemService;

import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.ToggleButton;

public class Sensor extends Fragment {


    static final int REQUEST_ENABLE_BT = 10;
    int mPairedDeviceCount = 0;
    int limit = 0;                  //센서 위험 한계값 설정
    Set<BluetoothDevice> mDevices;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mRemoteDevice;
    BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;
    String mStrDelimiter = "\n";
    char mCharDelimiter = '\n';
    Thread mWorkerThread = null;
    byte[] readBuffer;
    int readBufferPosition;
    //EditText mEditReceive, mEditSend;
    TextView mEditReceive, mEditSend;
    Button mButtonSend;
    ScrollView scrolldata;              // 스크롤뷰 선언

    MainActivity activit;
    public TextView tvShowNumbers;
    Context ct;
    int getlimit;


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        activit = (MainActivity)getActivity();
    }

    @Override
    public void onDetach(){
        super.onDetach();
        activit = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_sensor,container,false);
        final TextView tv = (TextView)view.findViewById(R.id.textView2);
        SeekBar seekBar  = (SeekBar)view.findViewById(R.id.seekBar1);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // onProgressChange - Seekbar 값 변경될때마다 호출
                Log.d("시크바", String.format("onProgressChanged 값 변경 중 : progress [%d] fromUser [%b]", progress, fromUser));
                tv.setText("거리: " +progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // onStartTeackingTouch - SeekBar 값 변경위해 첫 눌림에 호출
                Log.d("시크바", String.format("onStartTrackingTouch 값 변경 시작 : progress [%d]", seekBar.getProgress()));

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // onStopTrackingTouch - SeekBar 값 변경 끝나고 드래그 떼면 호출
                Log.d("시크바", String.format("onStopTrackingTouch 값 변경 종료: progress [%d]", seekBar.getProgress()));
                getlimit=seekBar.getProgress();
            }
        });



        ToggleButton bton_button = (ToggleButton) view.findViewById(R.id.BTon_button);

        bton_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bton_button.isChecked()){
                checkBluetooth();}
                else{
                    bluetoothOff();

                }
            }


        });




        return view;
        //return inflater.inflate(R.layout.fragment_sensor, container, false);
    }


    BluetoothDevice getDeviceFromBondedList(String name){
        BluetoothDevice selectedDevice = null;
        for (BluetoothDevice device : mDevices) {
            if(name.equals(device.getName())){
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }
    void sendData(String msg){
        msg += mStrDelimiter;
        try{
            mOutputStream.write(msg.getBytes());
        }catch(Exception e){
            Toast.makeText(activit.getApplicationContext(), "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            activit.finish();
        }
    }
    void connectToSelectedDevice(String selectedDeviceName){
        mRemoteDevice = getDeviceFromBondedList(selectedDeviceName);
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        try{
            mSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect();
            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();
            beginListenForData();
        }catch(Exception e){
            Toast.makeText(activit.getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            activit.finish();
        }
    }
    void beginListenForData(){
        final Handler handler = new Handler();
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        mWorkerThread = new Thread(new Runnable()
        {
            public void run(){
                while(!Thread.currentThread().isInterrupted()){
                    try {
                        int bytesAvailable = mInputStream.available();
                        if(bytesAvailable > 0){
                            byte[] packetBytes = new byte[bytesAvailable];
                            mInputStream.read(packetBytes);
                            for(int i = 0; i < bytesAvailable; i++){
                                byte b = packetBytes[i];
                                if(b == mCharDelimiter){
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0,
                                            encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    String data2= data.trim();// data2에 data의 개행문자를 제거한 값을 넘겨줌
                                    double getnum = Double.parseDouble(data2); // getnum에 data2를 double 형식으로 바꿔서 값을 넘겨줌
                                    readBufferPosition = 0;
                                    handler.post(new Runnable(){
                                        public void run(){
                                            System.out.println(getnum);
                                            if(getnum<getlimit){
                                                ToneGenerator tone= new ToneGenerator(AudioManager.STREAM_MUSIC,ToneGenerator.MAX_VOLUME);
                                                tone.startTone(ToneGenerator.TONE_DTMF_C,500);

                                            }
                                            Log.i("적외선 데이터", data);

                                        }
                                    });
                                }
                                else{
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex){
                        Toast.makeText(activit.getApplicationContext(), "데이터 수신 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                        activit.finish();
                    }
                }
            }
        });
        mWorkerThread.start();
    }
    public void bluetoothOff(){
        mBluetoothAdapter.disable();
    }
    void selectDevice(){
        mDevices = mBluetoothAdapter.getBondedDevices();
        mPairedDeviceCount = mDevices.size();
        if(mPairedDeviceCount == 0){
            Toast.makeText(activit.getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            activit.finish();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activit);
        builder.setTitle("블루투스 장치 선택");
        List<String> listItems = new ArrayList<String>();
        for (BluetoothDevice device : mDevices) {
            listItems.add(device.getName());
        }
        listItems.add("취소");
        final CharSequence[] items =
                listItems.toArray(new CharSequence[listItems.size()]);
        builder.setItems(items, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int item){
                if(item == mPairedDeviceCount){
                    Toast.makeText(activit.getApplicationContext(), "연결할 장치를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                    //activit.finish();
                }
                else{
                    connectToSelectedDevice(items[item].toString());
                }
            }
        });
        builder.setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
    }
    void checkBluetooth(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            Toast.makeText(activit.getApplicationContext(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();
            activit.finish();
        }
        else {
            if (!mBluetoothAdapter.isEnabled()) {
                Toast.makeText(activit.getApplicationContext(), "현재 블루투스가 비활성 상태입니다.", Toast.LENGTH_LONG).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else
                selectDevice();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK){
                    selectDevice();
                }
                else if(resultCode == RESULT_CANCELED){
                    Toast.makeText(activit.getApplicationContext(), "블루투스를 사용할 수 없어 프로그램을 종료합니다.",
                            Toast.LENGTH_LONG).show();
                    activit.finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}