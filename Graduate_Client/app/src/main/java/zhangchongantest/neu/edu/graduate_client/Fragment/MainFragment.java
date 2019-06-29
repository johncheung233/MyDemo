package zhangchongantest.neu.edu.graduate_client.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import zhangchongantest.neu.edu.graduate_client.Config;
import zhangchongantest.neu.edu.graduate_client.R;
import zhangchongantest.neu.edu.graduate_client.SocketConnect.ConnectManager;

/**
 * Created by Cheung SzeOn on 2019/3/29.
 */

public class MainFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, CallBackFromActivity, View.OnTouchListener {
    private ImageView im_toBlueTooth, im_toPay, im_unknow;
    private TextView tv_fragmentCarId, tv_fragmentParkingId, tv_fragmentBondStatus;
    private CallBackFromFragment callBackFromFragment;
    private boolean isPrepared;
    private ViewFlipper viewFlipper;
    private float startX;
    private Context context;
    private int resId[] = {R.mipmap.viewflipper1, R.mipmap.viewflipper2,
            R.mipmap.viewflipper3, R.mipmap.viewflipper4};

    public MainFragment() {
        super();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        if (!viewFlipper.isFlipping()) {
            viewFlipper.startFlipping();
        }
        //TODO
//        if (ConnectManager.getInstance().getPersonalObjectConfig().getCarID()!=null){
//            String carId = ConnectManager.getInstance().getPersonalObjectConfig().getCarID();
//            ed_carId.setText(carId.substring(1,carId.length()));
//            carIdAdapter.getCount();
//            for (int index=0;index<carIdAdapter.getCount();index++)
//            {
//                if (carIdAdapter.getItem(index).contains(String.valueOf(carId.charAt(0)))){
//                    sp_carId.setSelection(index);
//                }
//            }
//        }else {
//            ed_carId.setText("");
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CallBackFromFragment) {
            callBackFromFragment = (CallBackFromFragment) context;

        } else {
            throw new RuntimeException(context.toString() + " must implement Callback");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, null);
        setContext(view.getContext());
        initView(view);
        isPrepared = true;
        lazyLoad();
        return view;
    }

    private void initView(View view) {
        tv_fragmentCarId = view.findViewById(R.id.textView_fragmentCarId);
        tv_fragmentParkingId = view.findViewById(R.id.textView_fragmentParkingId);
        tv_fragmentBondStatus = view.findViewById(R.id.textView_fragmentBondStatus);
        im_toBlueTooth = (ImageView) view.findViewById(R.id.imageView_toBlueTooth);
        im_toPay = (ImageView) view.findViewById(R.id.imageView_parkOut);
        //im_unknow = (ImageView)view.findViewById(R.id.imageView_unknow);
        im_toBlueTooth.setOnClickListener(this);
        im_toPay.setOnClickListener(this);
//        im_unknow.setOnClickListener(this);
        viewFlipper = (ViewFlipper) view.findViewById(R.id.viewFlipper);
        for (int index : resId) {
            viewFlipper.addView(getImageView(view, index));
        }
        viewFlipper.setFlipInterval(5000);
        viewFlipper.setInAnimation(getContext(), R.anim.viewflipper_left_in);
        viewFlipper.setOutAnimation(getContext(), R.anim.viewflipper_right_out);
        //viewFlipper.startFlipping();
        viewFlipper.setOnTouchListener(this);
    }

    private ImageView getImageView(View view, int resId) {
        ImageView imageView = new ImageView(view.getContext());
        imageView.setImageResource(resId);
        return imageView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.button_carParkIn:
//                if (ConnectManager.getInstance().getPersonalObjectConfig().getAccountName() == null
//                        || ConnectManager.getInstance().getPersonalObjectConfig().getAccountPassword() == null) {
//                    callBackFromFragment.showWarningDialog(getResources().getString(R.string.unLoginWarning));
//                    return;
//                }
//                if (ConnectManager.getInstance().getPersonalObjectConfig().getCarID() != null) {
//                    callBackFromFragment.showWarningDialog(getResources().getString(R.string.repaetCarin));
//                    return;
//                }
//                if (TextUtils.isEmpty(ed_carId.getText())) {
//                    callBackFromFragment.showWarningDialog(getResources().getString(R.string.carIdEmpty));
//                    return;
//                }
//                if (!matchCarId(ed_carId.getText().toString())) {
//                    callBackFromFragment.showWarningDialog(getResources().getString(R.string.carIdWarning));
//                    return;
//                }
//                callBackFromFragment.showLoadingDialog();
//                strbuilder.append(Config.CMD_CAR_IN)
//                        .append(Config.MSG_SPLIT)
//                        .append(sp_carId.getSelectedItem() + ed_carId.getText().toString())
//                        .append(Config.MSG_SPLIT)
//                        .append(sp_carType.getSelectedItem())
//                        .append(Config.End_char);
//                ConnectManager.getInstance().getPersonalObjectConfig().setRequestMsg(strbuilder.toString());
//                callBackFromFragment.sendCommand();
//                break;
            case R.id.imageView_toBlueTooth:
                callBackFromFragment.startOtherActivity(Config.START_BLUETOOTH_ACTIVITY);
                break;
            case R.id.imageView_parkOut:
                String carID = ConnectManager.getInstance().getPersonalObjectConfig().getCarID();
                if (TextUtils.isEmpty(carID) || Config.INITCHECKNULL.equals(carID)) {
                    callBackFromFragment.showWarningDialog(getResources().getString(R.string.unCarin));
                    return;
                }
                callBackFromFragment.setCommandAndSend(Config.SET_PARKOUT_COMMAND, null, null, null);
                break;
//            case R.id.imageView_unknow:
//
//                break;
        }
    }

    public void setCallBackFromFragment(CallBackFromFragment callBackFromFragment) {
        this.callBackFromFragment = callBackFromFragment;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (view.getId()) {
//            case R.id.spinner_carType:
//                ConnectManager.getInstance().getPersonalObjectConfig().setCarType(parent.getItemAtPosition(position).toString());
//                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //ConnectManager.getInstance().getPersonalObjectConfig().setCarType(parent.getItemAtPosition(0).toString());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callBackFromFragment = null;
    }

    @Override
    public void feedBackMsg() {
        String carID = ConnectManager.getInstance().getPersonalObjectConfig().getCarID();
        String parkingID = ConnectManager.getInstance().getPersonalObjectConfig().getBookingSpaceId();
        String bondStatus = ConnectManager.getInstance().getPersonalObjectConfig().getBondStatus();
        if (!TextUtils.isEmpty(carID) && !Config.INITCHECKNULL.equals(carID)) {
            tv_fragmentCarId.setText(carID);
        }else {
            tv_fragmentCarId.setText(getResources().getString(R.string.unknow));
        }
        if (!TextUtils.isEmpty(parkingID) && !Config.INITCHECKNULL.equals(parkingID)) {
            tv_fragmentParkingId.setText(parkingID);
        }else {
            tv_fragmentParkingId.setText(getResources().getString(R.string.unknow));
        }
        if (!TextUtils.isEmpty(bondStatus) && !Config.INITCHECKNULL.equals(bondStatus)) {
            tv_fragmentBondStatus.setText(bondStatus);
        } else {
            tv_fragmentBondStatus.setText("未绑定");
        }
    }

    @Override
    public void viewScrolling() {
        //Log.e(Config.TAG,"viewSrolling");
        if (viewFlipper != null) {
            if (viewFlipper.isFlipping()) {
                viewFlipper.stopFlipping();
                // Log.e(Config.TAG,"stopFlipping");
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.getParent().requestDisallowInterceptTouchEvent(true);
                startX = event.getX();
                Log.e(Config.TAG, "ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                v.getParent().requestDisallowInterceptTouchEvent(true);
                //Log.e(Config.TAG,"ACTION_UP getAction:"+event.getAction()+" startX:"+startX);
                if (event.getX() - startX > 100) {    //向右滑
                    Log.e(Config.TAG, "right");
                    viewFlipper.showNext();
                }
                if (startX - event.getX() > 100) {    //向左滑
                    Log.e(Config.TAG, "left");
                    viewFlipper.setInAnimation(getContext(), R.anim.viewflipper_right_in);
                    viewFlipper.setOutAnimation(getContext(), R.anim.viewflipper_left_out);
                    viewFlipper.showPrevious();
                    viewFlipper.setInAnimation(getContext(), R.anim.viewflipper_left_in);
                    viewFlipper.setOutAnimation(getContext(), R.anim.viewflipper_right_out);
                }
                break;
        }
        return true;
    }

    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


}
