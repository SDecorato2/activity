package com.nightonke.saver.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.balysv.materialripple.MaterialRippleLayout;
import com.dd.CircularProgressButton;
import com.github.johnpersano.supertoasts.SuperToast;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nightonke.saver.BuildConfig;
import com.nightonke.saver.R;
import com.nightonke.saver.model.AppUpdateManager;
import com.nightonke.saver.model.Logo;
import com.nightonke.saver.model.RecordManager;
import com.nightonke.saver.model.SettingManager;
import com.nightonke.saver.model.User;
import com.nightonke.saver.ui.RiseNumberTextView;
import com.nightonke.saver.util.CoCoinUtil;
import com.nightonke.saver.util.EmailValidator;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Switch;
import com.rey.material.widget.Switch.OnCheckedChangeListener;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountBookSettingActivity extends AppCompatActivity
    implements
        View.OnClickListener,
        ColorChooserDialog.ColorCallback,
        OnCheckedChangeListener {

    private final int UPDATE_LOGO = 0;
    private final int UPDATE_IS_MONTH_LIMIT = 1;
    private final int UPDATE_MONTH_LIMIT = 2;
    private final int UPDATE_IS_COLOR_REMIND = 3;
    private final int UPDATE_MONTH_WARNING = 4;
    private final int UPDATE_REMIND_COLOR = 5;
    private final int UPDATE_IS_FORBIDDEN = 6;
    private final int UPDATE_ACCOUNT_BOOK_NAME = 7;
    private final int UPDATE_ACCOUNT_BOOK_PASSWORD = 8;
    private final int UPDATE_SHOW_PICTURE = 9;
    private final int UPDATE_IS_HOLLOW = 10;
    private final int UPDATE_LOGO_ID = 11;

    private Context mContext;

    private MaterialIconView back;

    private File logoFile;
    private CircleImageView logo;
    private Bitmap logoBitmap;

    private MaterialEditText registerUserName;
    private MaterialEditText registerUserEmail;
    private MaterialEditText registerPassword;
    private MaterialEditText loginUserName;
    private MaterialEditText loginPassword;

    private MaterialRippleLayout profileLayout;
    private MaterialIconView userNameIcon;
    private MaterialIconView userEmailIcon;
    private TextView userName;
    private TextView userEmail;
    private TextView loginButton;
    private RiseNumberTextView expense;
    private TextView expenseTV;
    private RiseNumberTextView records;
    private TextView recordsTV;

    private MaterialRippleLayout monthLayout;
    private MaterialIconView monthIcon;
    private MaterialIconView monthMaxExpenseIcon;
    private MaterialIconView monthColorRemindIcon;
    private MaterialIconView monthWarningIcon;
    private MaterialIconView monthColorRemindTypeIcon;
    private MaterialIconView monthForbiddenIcon;
    private Switch monthSB;
    private Switch monthColorRemindSB;
    private Switch monthForbiddenSB;
    private RiseNumberTextView monthMaxExpense;
    private RiseNumberTextView monthWarning;
    private MaterialIconView monthColorRemindSelect;
    private TextView monthLimitTV;
    private TextView monthMaxExpenseTV;
    private TextView monthColorRemindTV;
    private TextView monthWarningTV;
    private TextView monthColorRemindTypeTV;
    private TextView monthForbiddenTV;

    private MaterialRippleLayout accountBookNameLayout;
    private TextView accountBookNameTV;
    private TextView accountBookName;

    private MaterialRippleLayout changePasswordLayout;
    private TextView changePasswordTV;

    private MaterialRippleLayout sortTagsLayout;
    private TextView sortTagsTV;

    private MaterialRippleLayout showPictureLayout;
    private MaterialIconView showPictureIcon;
    private Switch showPictureSB;
    private TextView showPictureTV;

    private MaterialRippleLayout hollowLayout;
    private MaterialIconView hollowIcon;
    private Switch hollowSB;
    private TextView hollowTV;

    private MaterialRippleLayout updateLayout;
    private TextView currentVersionTV;
    private TextView canBeUpdatedTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        setContentView(R.layout.activity_account_book_setting);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.statusBarColor));
        } else{
            // do something for phones running an SDK before lollipop
            View statusBarView = findViewById(R.id.status_bar_view);
            statusBarView.getLayoutParams().height = CoCoinUtil.getStatusBarHeight();
        }

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        SuperToast.cancelAllSuperToasts();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_image:
                changeLogo();
                break;
        }
    }

    public void v1(){
        setTipeD(isChecked,view);
        setTipeE(isChecked,view);
    }

    public void v2(){
        setTipeA(isChecked,view);
        setTipeB(isChecked,view);
        setTipeC(isChecked,view);
    }

// switch change listener///////////////////////////////////////////////////////////////////////////
    @Override

    public void onCheckedChanged(Switch view, boolean isChecked) {
        boolean b= true;
        if(b){
            v2();
        }
        else{
          v1();
        }
    }

    public int checktype(Switch view){
        return view.getId();
    }

    public void support(boolean  isChecked){
        if (isChecked && SettingManager.getInstance().getIsMonthLimit()) {
            monthColorRemindSelect.setEnabled(true);
            monthColorRemindSelect
                    .setColor(SettingManager.getInstance().getRemindColor());
            monthWarning.setEnabled(true);
            monthWarning.setTextColor(
                    ContextCompat.getColor(mContext, R.color.drawer_text));
        } else {
            monthColorRemindSelect.setEnabled(false);
            monthColorRemindSelect
                    .setColor(mContext.getResources().getColor(R.color.my_gray));
            monthWarning.setEnabled(false);
            monthWarning.setTextColor(
                    ContextCompat.getColor(mContext, R.color.my_gray));
        }
        setTVEnable(monthColorRemindTypeTV, isChecked
                && SettingManager.getInstance().getIsMonthLimit());
        setTVEnable(monthWarningTV, isChecked
                && SettingManager.getInstance().getIsMonthLimit());
    }
    public void setTipeA(boolean isChecked,Switch view){
        if((view) == R.id.month_limit_enable_button ) {
            SettingManager.getInstance().setIsMonthLimit(isChecked);
            updateSettingsToServer(UPDATE_IS_MONTH_LIMIT);
            SettingManager.getInstance().setMainViewMonthExpenseShouldChange(true);
            SettingManager.getInstance().setMainViewRemindColorShouldChange(true);
            SettingManager.getInstance().setTodayViewMonthExpenseShouldChange(true);
            setMonthState();
        }
    }
    public void setTipeB(boolean isChecked,Switch view){
        if(checktype(view) == R.id.month_color_remind_button ) {
            SettingManager.getInstance().setIsColorRemind(isChecked);
            updateSettingsToServer(UPDATE_IS_COLOR_REMIND);
            SettingManager.getInstance().setMainViewRemindColorShouldChange(true);
            setIconEnable(monthColorRemindIcon, isChecked
                    && SettingManager.getInstance().getIsMonthLimit());
            setIconEnable(monthColorRemindTypeIcon, isChecked
                    && SettingManager.getInstance().getIsMonthLimit());
            setIconEnable(monthWarningIcon, isChecked
                    && SettingManager.getInstance().getIsMonthLimit());
            support(isChecked);
            setTVEnable(monthColorRemindTypeTV, isChecked
                    && SettingManager.getInstance().getIsMonthLimit());
            setTVEnable(monthWarningTV, isChecked
                    && SettingManager.getInstance().getIsMonthLimit());
        }
    }
    public void setTipeC(boolean isChecked,Switch view) {
        if(checktype(view) == R.id.month_forbidden_button ) {
            SettingManager.getInstance().setIsForbidden(isChecked);
            updateSettingsToServer(UPDATE_IS_FORBIDDEN);
            setIconEnable(monthForbiddenIcon, isChecked
                    && SettingManager.getInstance().getIsMonthLimit());
        }
    }
    public void setTipeD(boolean isChecked,Switch view) {
        if(checktype(view) ==R.id.whether_show_picture_button ) {
            SettingManager.getInstance().setShowPicture(isChecked);
            updateSettingsToServer(UPDATE_SHOW_PICTURE);
            setShowPictureState(isChecked);
        }
    }
    public void setTipeE(boolean isChecked,Switch view) {
        if(checktype(view) == R.id.whether_show_circle_button) {
            SettingManager.getInstance().setIsHollow(isChecked);
            updateSettingsToServer(UPDATE_IS_HOLLOW);
            setHollowState(isChecked);
            SettingManager.getInstance().setTodayViewPieShouldChange(Boolean.TRUE);
        }
    }

// Load logo from local/////////////////////////////////////////////////////////////////////////////
    private void loadLogo() {
        User user = BmobUser.getCurrentUser(CoCoinApplication.getAppContext(), User.class);
        if (user != null) {
            try {
                File logoFile = new File(CoCoinApplication.getAppContext().getFilesDir() + CoCoinUtil.LOGO_NAME);
                if (!logoFile.exists()) {
                    // the local logo file is missed
                    // try to get from the server
                    BmobQuery<Logo> bmobQuery = new BmobQuery();
                    bmobQuery.addWhereEqualTo("objectId", user.getLogoObjectId());
                    bmobQuery.findObjects(CoCoinApplication.getAppContext()
                            , new FindListener<Logo>() {
                                @Override
                                public void onSuccess(List<Logo> object) {
                                    // there has been an old logo in the server/////////////////////////////////////////////////////////
                                    String url = object.get(0).getFile().getFileUrl(CoCoinApplication.getAppContext());
                                    if (BuildConfig.DEBUG) Log.d("CoCoin", "Logo in server: " + url);
                                    Ion.with(CoCoinApplication.getAppContext()).load(url)
                                            .write(new File(CoCoinApplication.getAppContext().getFilesDir()
                                                    + CoCoinUtil.LOGO_NAME))
                                            .setCallback(new FutureCallback<File>() {
                                                @Override
                                                public void onCompleted(Exception e, File file) {
                                                    logo.setImageBitmap(BitmapFactory.decodeFile(
                                                            CoCoinApplication.getAppContext().getFilesDir()
                                                                    + CoCoinUtil.LOGO_NAME));
                                                }
                                            });
                                }
                                @Override
                                public void onError(int code, String msg) {
                                    // the picture is lost
                                    if (BuildConfig.DEBUG) Log.d("CoCoin", "Can't find the old logo in server.");
                                }
                            });
                } else {
                    // the user logo is in the storage
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(logoFile));
                    logo.setImageBitmap(b);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            // use the default logo
            logo.setImageResource(R.drawable.default_user_logo);
        }
    }

// change the user logo/////////////////////////////////////////////////////////////////////////////
    private void changeLogo() {
        User user = BmobUser.getCurrentUser(CoCoinApplication.getAppContext(), User.class);
        if (user == null) {
            new MaterialDialog.Builder(this)
                    .iconRes(R.drawable.cocoin_logo)
                    .typeface(CoCoinUtil.GetTypeface(), CoCoinUtil.GetTypeface())
                    .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                    .title(R.string.login_first_title)
                    .content(R.string.login_first_content)
                    .positiveText(R.string.ok)
                    .neutralText(R.string.cancel)
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog,
                                            @NonNull DialogAction which) {
                            if (which == DialogAction.POSITIVE) {
                                userOperator();
                            }
                        }
                    })
                    .show();
            return;
        }
        new MaterialDialog.Builder(this)
                .iconRes(R.drawable.cocoin_logo)
                .typeface(CoCoinUtil.GetTypeface(), CoCoinUtil.GetTypeface())
                .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                .title(R.string.change_logo_title)
                .content(R.string.change_logo_content)
                .positiveText(R.string.from_gallery)
                .negativeText(R.string.from_camera)
                .neutralText(R.string.cancel)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                            intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            startActivityForResult(intent1, 1);
                        } else if (which == DialogAction.NEGATIVE) {
                            Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(new File(CoCoinApplication.getAppContext().getFilesDir()
                                            + CoCoinUtil.LOGO_NAME)));
                            startActivityForResult(intent2, 2);
                        }
                    }
                })
                .show();
    }

// Crop a picture///////////////////////////////////////////////////////////////////////////////////
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX : aspectY
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY the height and width
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

// After select a picture///////////////////////////////////////////////////////////////////////////
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                // after select from gallery
                if (resultCode == RESULT_OK) {
                    cropPhoto(data.getData());
                }
                break;
            case 2:
                // after taking a photo
                if (resultCode == RESULT_OK) {
                    File temp = new File(CoCoinApplication.getAppContext().getFilesDir() + CoCoinUtil.LOGO_NAME);
                    cropPhoto(Uri.fromFile(temp));
                }
                break;
            case 3:
                // after crop the picture
                if (data != null) {
                    Bundle extras = data.getExtras();
                    logoBitmap = extras.getParcelable("data");
                    if(logoBitmap != null) {
                        SettingManager.getInstance().setHasLogo(true);
                        setPicToView(logoBitmap);
                        SettingManager.getInstance().setTodayViewLogoShouldChange(true);
                        logo.setImageBitmap(logoBitmap);
                    }
                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

// Storage a picture////////////////////////////////////////////////////////////////////////////////
    private void setPicToView(Bitmap mBitmap) {
        FileOutputStream b = null;
        File file = new File(CoCoinApplication.getAppContext().getFilesDir() + CoCoinUtil.LOGO_NAME);
        String fileName = file.getAbsolutePath();  // get logo position
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);  // write the data to file
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // close
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadLogoToServer();
        }
    }

// download logo to local///////////////////////////////////////////////////////////////////////////
    private void downloadLogoFromServer() {
        User user = getCurrentUser();
        if (user.getLogoObjectId() == null) {
            // the user has no logo
            return;
        }
        BmobQuery<Logo> bmobQuery = new BmobQuery();
        bmobQuery.addWhereEqualTo("objectId", user.getLogoObjectId());
        bmobQuery.findObjects(CoCoinApplication.getAppContext()
                , new FindListener<Logo>() {
            @Override
            public void onSuccess(List<Logo> object) {
// there has been an old logo in the server/////////////////////////////////////////////////////////
                Log.d("Saver", "There is an old logo");
                String url = object.get(0).getFile().getUrl();
                Ion.with(CoCoinApplication.getAppContext()).load(url)
                        .write(new File(CoCoinApplication.getAppContext().getFilesDir()
                                + CoCoinUtil.LOGO_NAME))
                                .setCallback(new FutureCallback<File>() {
                                    @Override
                                    public void onCompleted(Exception e, File file) {
                                        Bitmap bitmap = BitmapFactory.
                                                decodeFile(CoCoinApplication.getAppContext().getFilesDir()
                                                        + CoCoinUtil.LOGO_NAME);
                                        if (bitmap == null) {
                                            Log.d("Saver", "Logo misses");
                                        } else {
                                            logo.setImageBitmap(bitmap);
                                        }
                                        SettingManager.getInstance().setHasLogo(true);
                                    }
                                });
                SettingManager.getInstance().setTodayViewLogoShouldChange(true);
            }
            @Override
            public void onError(int code, String msg) {
                // the picture is lost
                Log.d("Saver", "Can't find the old logo in server.");
            }
        });
    }

// update a logo to server//////////////////////////////////////////////////////////////////////////
    private void uploadLogoToServer() {
        if (!SettingManager.getInstance().getHasLogo()) {
            // the user haven't set the logo
            return;
        }
        final File file = new File(CoCoinApplication.getAppContext().getFilesDir() + CoCoinUtil.LOGO_NAME);
        final User user = getCurrentUser();
// if login/////////////////////////////////////////////////////////////////////////////////////////
        if (user != null) {
            if (user.getLogoObjectId() != "") {
// if the logo id is not null, then there must be a logo and a logo file in the server//////////////
// judge whether there is an old logo of the same user//////////////////////////////////////////////
                BmobQuery<Logo> bmobQuery = new BmobQuery();
                bmobQuery.addWhereEqualTo("objectId", user.getLogoObjectId());
                bmobQuery.findObjects(CoCoinApplication.getAppContext()
                        , new FindListener<Logo>() {
                    @Override
                    public void onSuccess(List<Logo> object) {
// there has been an old logo in the server/////////////////////////////////////////////////////////
// then there must be an old logo file in server////////////////////////////////////////////////////
// then we should delete the old one////////////////////////////////////////////////////////////////
                        Log.d("Saver", "There is an old logo");
                        String url = object.get(0).getFile().getUrl();
                        BmobFile oldLogoFile = new BmobFile();
                        oldLogoFile.setUrl(url);
                        oldLogoFile.delete(CoCoinApplication.getAppContext(), new DeleteListener() {
                            @Override
                            public void onSuccess() {
                                Log.d("Saver", "Successfully delete the old logo.");
// after delete, we should upload a new logo file///////////////////////////////////////////////////
                                final BmobFile newLogoFile = new BmobFile(file);
                                newLogoFile.uploadblock(CoCoinApplication.getAppContext(),
                                        new UploadFileListener() {
                                    @Override
                                    public void onSuccess() {
// after upload the new logo file, we should put the new logo the Logo table////////////////////////
                                        Logo newLogo = new Logo(newLogoFile);
                                        newLogo.update(CoCoinApplication.getAppContext(),
                                                user.getLogoObjectId(), new UpdateListener() {
                                                    @Override
                                                    public void onSuccess() {
                                                        Log.d("Saver", "Update logo successfully");
                                                    }

                                                    @Override
                                                    public void onFailure(int arg0, String arg1) {
                                                        Log.d("Saver", "Update logo failed " + arg1);
                                                    }
                                                });
                                    }
                                    @Override
                                    public void onProgress(Integer arg0) {
                                    }
                                    @Override
                                    public void onFailure(int arg0, String arg1) {
                                        Log.d("Saver", "Upload failed " + arg1);
                                    }
                                });
                            }
                            @Override
                            public void onFailure(int code, String msg) {
                                Log.d("Saver", "Fail to delete the old logo. " + msg);
                            }
                        });
                    }
                    @Override
                    public void onError(int code, String msg) {
                        // the picture is lost
                        Log.d("Saver", "Can't find the old logo in server.");
                    }
                });
            } else {
// the user has no logo before//////////////////////////////////////////////////////////////////////
                final BmobFile newLogoFile = new BmobFile(file);
                newLogoFile.uploadblock(CoCoinApplication.getAppContext(), new UploadFileListener() {
                    @Override
                    public void onSuccess() {
                        String url = newLogoFile.getFileUrl(CoCoinApplication.getAppContext());
                        Log.d("Saver", "Upload successfully " + url);
                        final Logo newLogo = new Logo(newLogoFile);
                        newLogo.save(CoCoinApplication.getAppContext(), new SaveListener() {
                            @Override
                            public void onSuccess() {
                                Log.d("Saver", "Save the new logo successfully.");
                                SettingManager.getInstance().setLogoObjectId(newLogo.getObjectId());
                                updateSettingsToServer(UPDATE_LOGO_ID);
                            }
                            @Override
                            public void onFailure(int i, String s) {
                                Log.d("Saver", "Save the new logo fail.");
                            }
                        });
                    }
                    @Override
                    public void onProgress(Integer arg0) {}
                    @Override
                    public void onFailure(int arg0, String arg1) {Log.d("Saver", "Upload failed " + arg1);}
                });
            }
        }
    }

// the user's operation when clicking the first card view///////////////////////////////////////////
    private void userOperator() {
        if (!SettingManager.getInstance().getLoggenOn()) {
            // register or log on
            new MaterialDialog.Builder(this)
                    .iconRes(R.drawable.cocoin_logo)
                    .typeface(CoCoinUtil.GetTypeface(), CoCoinUtil.GetTypeface())
                    .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                    .title(R.string.welcome)
                    .content(R.string.login_or_register)
                    .positiveText(R.string.login)
                    .negativeText(R.string.register)
                    .neutralText(R.string.cancel)
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog,
                                            @NonNull DialogAction which) {
                            if (which.equals(DialogAction.POSITIVE)) {
                                userLogin();
                            } else if (which.equals(DialogAction.NEGATIVE)) {
                                userRegister();
                            } else {
                                dialog.dismiss();
                            }
                        }
                    })
                    .show();
        } else {
            // log out or user operate
            new MaterialDialog.Builder(this)
                    .iconRes(R.drawable.cocoin_logo)
                    .typeface(CoCoinUtil.GetTypeface(), CoCoinUtil.GetTypeface())
                    .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                    .title(mContext.getResources().getString(R.string.hi)
                            + SettingManager.getInstance().getUserName())
                    .content(R.string.whether_logout)
                    .positiveText(R.string.log_out)
                    .neutralText(R.string.cancel)
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog,
                                            @NonNull DialogAction which) {
                            if (which.equals(DialogAction.POSITIVE)) {
                                userLogout();
                            } else {
                                dialog.dismiss();
                            }
                        }
                    })
                    .show();
        }
    }

// User log out/////////////////////////////////////////////////////////////////////////////////////
    private void userLogout() {
        BmobUser.logOut(CoCoinApplication.getAppContext());
        SettingManager.getInstance().setTodayViewInfoShouldChange(true);
        SettingManager.getInstance().setLoggenOn(false);
        SettingManager.getInstance().setUserName(null);
        SettingManager.getInstance().setUserEmail(null);
        logo.setImageResource(R.drawable.default_user_logo);
        updateViews();
        showToast(8, "");
    }

// User login///////////////////////////////////////////////////////////////////////////////////////
    MaterialDialog loginDialog;
    View loginDialogView;
    CircularProgressButton loginDialogButton;

    public void b1(){
        @Override
        public void onSuccess() {
            loginDialog.setCancelable(true);
            loginDialogButton.setProgress(0);
            loginDialogButton.setIdleText(getResourceString(R.string.login_complete));
// login successfully through user name/////////////////////////////////////////////////////////////
            SettingManager.getInstance().setTodayViewInfoShouldChange(true);
            User loginUser =
                    BmobUser.getCurrentUser(CoCoinApplication.getAppContext(), User.class);
            if (!CoCoinApplication.getAndroidId().equals(loginUser.getAndroidId())) {
// 2 users on one mobile////////////////////////////////////////////////////////////////////////////
                showToast(7, "unique...");
                return;
            }
            SettingManager.getInstance().setLoggenOn(true);
            SettingManager.getInstance().setUserName(loginUserName.getText().toString());
            SettingManager.getInstance().setUserEmail(
                    loginUser.getEmail());
            updateViews();
            // use a new method
//                        RecordManager.updateOldRecordsToServer();
            whetherSyncSettingsFromServer();
            showToast(6, loginUserName.getText().toString());
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (loginDialog != null) loginDialog.dismiss();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS,0);
                }
            }, 500);
        }
    }

    public void b2(){
        @Override
        public void onSuccess() {
            loginDialog.setCancelable(true);
            loginDialogButton.setProgress(0);
            loginDialogButton.setIdleText(getResourceString(R.string.login_complete));
// login successfully through user email////////////////////////////////////////////////////////////
            SettingManager.getInstance().setTodayViewInfoShouldChange(true);
            User loginUser =
                    BmobUser.getCurrentUser(CoCoinApplication.getAppContext(), User.class);
            if (!CoCoinApplication.getAndroidId().equals(loginUser.getAndroidId())) {
// 2 users on one mobile////////////////////////////////////////////////////////////////////////////
                showToast(7, "unique...");
                return;
            }
            String userName = loginUser.getUsername();
            SettingManager.getInstance().setLoggenOn(true);
            SettingManager.getInstance().setUserName(userName);
            SettingManager.getInstance().setUserEmail(loginUserName.getText().toString());
            SettingManager.getInstance().setUserPassword(loginPassword.getText().toString());
            updateViews();
            // use a new method
//                                RecordManager.updateOldRecordsToServer();
            whetherSyncSettingsFromServer();
            showToast(6, userName);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (loginDialog != null) loginDialog.dismiss();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS,0);
                }
            }, 500);
        }
    }

    public void b3(){
        @Override
        public void onFailure(int code, String msg) {
// try with user email//////////////////////////////////////////////////////////////////////////////
            user.setEmail(loginUserName.getText().toString());
            user.login(CoCoinApplication.getAppContext(), new SaveListener() {
                b2();
                // login fail through user name and email///////////////////////////////////////////////////////////
                @Override
                public void onFailure(int code, String msg) {
                    loginDialog.setCancelable(true);
                    loginDialogButton.setProgress(0);
                    String tip = getResourceString(R.string.network_disconnection);
                    if (msg.charAt(0) == 'u') tip = getResourceString(R.string.user_name_or_password_incorrect);
                    if (msg.charAt(1) == 'n') tip = getResourceString(R.string.user_mobile_exist);
                    loginDialogButton.setIdleText(tip);
                }
            });
        }
    }

    private void userLogin() {
        loginDialog = new MaterialDialog.Builder(this)
                .title(R.string.go_login)
                .typeface(CoCoinUtil.GetTypeface(), CoCoinUtil.GetTypeface())
                .customView(R.layout.dialog_user_login, true)
                .build();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

        loginDialogView = loginDialog.getCustomView();
        loginDialogButton = (CircularProgressButton) loginDialogView.findViewById(R.id.button);
        loginDialogButton.isIndeterminateProgressMode();
        loginDialogButton.setProgress(0);
        loginDialogButton.setTypeface(CoCoinUtil.GetTypeface());
        loginDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginDialog.setCancelable(false);
                loginDialogButton.setProgress(1);
// the user ask to login////////////////////////////////////////////////////////////////////////////
                final User user = new User();
                user.setUsername(loginUserName.getText().toString());
                user.setPassword(loginPassword.getText().toString());
                user.login(CoCoinApplication.getAppContext(), new SaveListener() {
// try with user name///////////////////////////////////////////////////////////////////////////////
                    b1();
                    // login fail through user name/////////////////////////////////////////////////////////////////////
                    b3();
                });
            }
        });

        final MDButton positiveAction = loginDialog.getActionButton(DialogAction.POSITIVE);
        positiveAction.setEnabled(false);

        TextView userNameTV
                = (TextView)loginDialog.getCustomView().findViewById(R.id.login_user_name_text);
        TextView userPasswordTV
                = (TextView)loginDialog.getCustomView().findViewById(R.id.login_password_text);
        userNameTV.setTypeface(CoCoinUtil.GetTypeface());
        userPasswordTV.setTypeface(CoCoinUtil.GetTypeface());

        loginUserName
                = (MaterialEditText)loginDialog.getCustomView().findViewById(R.id.login_user_name);
        loginPassword
                = (MaterialEditText)loginDialog.getCustomView().findViewById(R.id.login_password);

        loginUserName.setTypeface(CoCoinUtil.GetTypeface());
        loginUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                control(loginUserName,loginPassword);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        loginPassword.setTypeface(CoCoinUtil.GetTypeface());
        loginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                loginDialogButton.setEnabled(
                        0 < loginUserName.getText().toString().length()
                                && 0 < loginPassword.getText().toString().length());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        loginDialog.show();
    }
    public void control(MaterialEditText loginUserName, MaterialEditText loginPassword){
        loginDialogButton.setEnabled(
                0 < loginUserName.getText().toString().length()
                        && 0 < loginPassword.getText().toString().length());
    }

// User register////////////////////////////////////////////////////////////////////////////////////
    MaterialDialog registerDialog;
    View registerDialogView;
    CircularProgressButton registerDialogButton;


    public void n1(){
        @Override
        public void onSuccess() {
            registerDialogButton.setProgress(0);
            registerDialog.setCancelable(true);
            registerDialogButton.setIdleText(getResourceString(R.string.register_complete));
// if register successfully/////////////////////////////////////////////////////////////////////////
            SettingManager.getInstance().setLoggenOn(true);
            SettingManager.getInstance().setUserName(registerUserName.getText().toString());
            SettingManager.getInstance().setUserEmail(registerUserEmail.getText().toString());
            SettingManager.getInstance().setUserPassword(registerPassword.getText().toString());
            showToast(4, registerUserName.getText().toString());
// if login successfully////////////////////////////////////////////////////////////////////////////
            user.login(CoCoinApplication.getAppContext(), new SaveListener() {
                @Override
                public void onSuccess() {
                    SettingManager.getInstance().setTodayViewInfoShouldChange(true);
                    updateViews();
                    // use a new method
//                                RecordManager.updateOldRecordsToServer();
                }
                @Override
                public void onFailure(int code, String msg) {
// if login failed//////////////////////////////////////////////////////////////////////////////////
                }
            });
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkRegisterDialog();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS,0);
                }
            }, 500);
        }
    }

    public void n2(){
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean emailOK = emailValidator.validate(registerUserEmail.getText().toString());
            registerDialogButton.setEnabled(
                    0 < registerUserName.getText().toString().length()
                            && registerUserName.getText().toString().length() <= 16
                            && registerPassword.getText().toString().length() > 0
                            && emailOK);
            if (emailValidator.validate(registerUserEmail.getText().toString())) {
                registerUserEmail.validate();
            } else {
                registerUserEmail.invalidate();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public void n3(){
        public void onClick(View v) {
            registerDialogButton.setProgress(1);
            registerDialog.setCancelable(false);
// User register, a new user////////////////////////////////////////////////////////////////////////
            final User user = new User();
            // basic info
            user.setUsername(registerUserName.getText().toString());
            user.setPassword(registerPassword.getText().toString());
            user.setEmail(registerUserEmail.getText().toString());
            user.setAndroidId(CoCoinApplication.getAndroidId());
            if (BuildConfig.DEBUG) Log.d("CoCoin", "Android Id: " + user.getAndroidId());
            // settings info
            // user.setLogo();
            user.setIsMonthLimit(SettingManager.getInstance().getIsMonthLimit());
            user.setMonthLimit(SettingManager.getInstance().getMonthLimit());
            user.setIsColorRemind(SettingManager.getInstance().getIsColorRemind());
            user.setMonthWarning(SettingManager.getInstance().getMonthWarning());
            user.setRemindColor(SettingManager.getInstance().getRemindColor());
            user.setIsForbidden(SettingManager.getInstance().getIsForbidden());
            user.setAccountBookName(SettingManager.getInstance().getAccountBookName());
            user.setAccountBookPassword(SettingManager.getInstance().getPassword());
            // Todo store tag order
            user.setShowPicture(SettingManager.getInstance().getShowPicture());
            user.setIsHollow(SettingManager.getInstance().getIsHollow());
            user.setLogoObjectId("");
            user.signUp(CoCoinApplication.getAppContext(), new SaveListener() {
                n1();
                // if register failed///////////////////////////////////////////////////////////////////////////////
                @Override
                public void onFailure(int code, String msg) {
                    supportOnFailure(msg);
                }
            });
        }
    }


    public void n4(){
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean emailOK = emailValidator.validate(registerUserEmail.getText().toString());
            registerDialogButton.setEnabled(
                    0 < registerUserName.getText().toString().length()
                            && registerUserName.getText().toString().length() <= 16
                            && registerPassword.getText().toString().length() > 0
                            && emailOK);
            if (emailValidator.validate(registerUserEmail.getText().toString())) {
                registerUserEmail.validate();
            } else {
                registerUserEmail.invalidate();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public void n5(){
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean emailOK = emailValidator.validate(registerUserEmail.getText().toString());
            registerDialogButton.setEnabled(
                    0 < registerUserName.getText().toString().length()
                            && registerUserName.getText().toString().length() <= 16
                            && registerPassword.getText().toString().length() > 0
                            && emailOK);
            if (emailValidator.validate(registerUserEmail.getText().toString())) {
                registerUserEmail.validate();
            } else {
                registerUserEmail.invalidate();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private void userRegister() {
        registerDialog = new MaterialDialog.Builder(this)
                .title(R.string.go_register)
                .typeface(CoCoinUtil.GetTypeface(), CoCoinUtil.GetTypeface())
                .customView(R.layout.dialog_user_register, true)
                .build();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

        registerDialogView = registerDialog.getCustomView();
        registerDialogButton = (CircularProgressButton)registerDialogView.findViewById(R.id.button);
        registerDialogButton.setTypeface(CoCoinUtil.GetTypeface());
        registerDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            n3();
        });

        final MDButton positiveAction = registerDialog.getActionButton(DialogAction.POSITIVE);
        positiveAction.setEnabled(false);
        final EmailValidator emailValidator = new EmailValidator();

        TextView userNameTV
                = (TextView)registerDialog.getCustomView().findViewById(R.id.register_user_name_text);
        TextView userEmailTV
                = (TextView)registerDialog.getCustomView().findViewById(R.id.register_user_email_text);
        TextView userPasswordTV
                = (TextView)registerDialog.getCustomView().findViewById(R.id.register_password_text);
        userNameTV.setTypeface(CoCoinUtil.GetTypeface());
        userEmailTV.setTypeface(CoCoinUtil.GetTypeface());
        userPasswordTV.setTypeface(CoCoinUtil.GetTypeface());

        registerUserName
                = (MaterialEditText)registerDialog.getCustomView().findViewById(R.id.register_user_name);
        registerUserEmail
                = (MaterialEditText)registerDialog.getCustomView().findViewById(R.id.register_user_email);
        registerPassword
                = (MaterialEditText)registerDialog.getCustomView().findViewById(R.id.register_password);

        registerUserName.setTypeface(CoCoinUtil.GetTypeface());
        registerUserName.addTextChangedListener(new TextWatcher() {
            n2();
        });

        registerUserEmail.setTypeface(CoCoinUtil.GetTypeface());
        registerUserEmail.addTextChangedListener(new TextWatcher() {
            @Override
            n4();
        });

        registerPassword.setTypeface(CoCoinUtil.GetTypeface());
        registerPassword.addTextChangedListener(new TextWatcher() {
            n5();
        });

        registerDialog.show();
    }


    public void supportOnFailure(String msg){
        if (BuildConfig.DEBUG) Log.d("CoCoin", "Register failed: " + msg);
        String tip = getResourceString(R.string.network_disconnection);
        if (msg.charAt(1) == 's') tip = getResourceString(R.string.user_name_exist);
        if (msg.charAt(0) == 'e') tip = getResourceString(R.string.user_email_exist);
        if (msg.charAt(1) == 'n') tip = getResourceString(R.string.user_mobile_exist);
        registerDialogButton.setIdleText(tip);
        registerDialogButton.setProgress(0);
        registerDialog.setCancelable(true);
    }
    public void checkRegisterDialog() {
        if (registerDialog != null) registerDialog.dismiss();
    }

// Change account book name/////////////////////////////////////////////////////////////////////////
    private void changeAccountBookName() {
        new MaterialDialog.Builder(this)
                .theme(Theme.LIGHT)
                .typeface(CoCoinUtil.GetTypeface(), CoCoinUtil.GetTypeface())
                .title(R.string.set_account_book_dialog_title)
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(1, 16)
                .positiveText(R.string.submit)
                .input(SettingManager.getInstance().getAccountBookName()
                        , null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // local change
                        SettingManager.getInstance().setAccountBookName(input.toString());
                        SettingManager.getInstance().setTodayViewTitleShouldChange(true);
                        SettingManager.getInstance().setMainViewTitleShouldChange(true);
                        accountBookName.setText(input.toString());
                        // update change
                        User user = getCurrentUser();
                        if (user != null) {
                                updateSettingsToServer(UPDATE_ACCOUNT_BOOK_NAME);
                            } else {
                                // the new account book name is changed successfully
                                showToast(2, "");
                        }
                    }
                }).show();
    }

// Update some views when login/////////////////////////////////////////////////////////////////////
    private void updateViews() {
        setIconEnable(userNameIcon, SettingManager.getInstance().getLoggenOn());
        setIconEnable(userEmailIcon, SettingManager.getInstance().getLoggenOn());
        if (SettingManager.getInstance().getLoggenOn()) {
            userName.setText(SettingManager.getInstance().getUserName());
            userEmail.setText(SettingManager.getInstance().getUserEmail());
            loginButton.setText(mContext.getResources().getText(R.string.logout_button));
            loginButton.setBackgroundResource(R.drawable.button_logout);
        } else {
            userName.setText("");
            userEmail.setText("");
            loginButton.setText(mContext.getResources().getText(R.string.login_button));
            loginButton.setBackgroundResource(R.drawable.button_login);
        }
    }

// Start change account book password activity//////////////////////////////////////////////////////
// I put the update to server part in the change password activity but not here/////////////////////
    private void changePassword() {
        Intent intent = new Intent(mContext, EditPasswordActivity.class);
        startActivity(intent);
    }

// Start sort tags activity/////////////////////////////////////////////////////////////////////////
// I put the update to server part in the sort tag activity but not here////////////////////////////
    private void sortTags() {
        Intent intent = new Intent(mContext, TagSettingActivity.class);
        startActivity(intent);
    }


    public void z1(){
        if (newExpense < SettingManager.getInstance().getMonthWarning()) {
            SettingManager.getInstance().setMonthWarning(
                    ((int)(newExpense * 0.8) / 100 * 100));
            if (SettingManager.getInstance().getMonthWarning() < 100) {
                SettingManager.getInstance().setMonthWarning(100);
            }
            updateSettingsToServer(UPDATE_MONTH_WARNING);
            SettingManager.getInstance()
                    .setMainViewRemindColorShouldChange(true);
            monthWarning.setText(SettingManager
                    .getInstance().getMonthWarning().toString());
        }
    }

    public void z2(){
        if (SettingManager.getInstance().getIsMonthLimit()) {
            new MaterialDialog.Builder(mContext)
                    .theme(Theme.LIGHT)
                    .typeface(CoCoinUtil.GetTypeface(), CoCoinUtil.GetTypeface())
                    .title(R.string.set_month_expense_dialog_title)
                    .inputType(InputType.TYPE_CLASS_NUMBER)
                    .positiveText(R.string.submit)
                    .inputRange(3, 5)
                    .input(SettingManager.getInstance().getMonthLimit().toString()
                            , null, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    int newExpense = SettingManager.getInstance().getMonthLimit();
                                    if (input.length() != 0) {
                                        newExpense = Integer.parseInt(input.toString());
                                    }
                                    // the month limit must be smaller than the month warning
                                    z1();
                                    SettingManager.getInstance().setMonthLimit(newExpense);
                                    updateSettingsToServer(UPDATE_MONTH_LIMIT);
                                    SettingManager.getInstance()
                                            .setTodayViewMonthExpenseShouldChange(true);
                                    SettingManager.getInstance()
                                            .setMainViewMonthExpenseShouldChange(true);
                                    monthMaxExpense.withNumber(SettingManager.getInstance()
                                            .getMonthLimit()).setDuration(1000).start();
                                }
                            }).show();
        }
    }

    public void z3(){
        if (SettingManager.getInstance().getIsMonthLimit()
                && SettingManager.getInstance().getIsColorRemind()) {
            new MaterialDialog.Builder(mContext)
                    .theme(Theme.LIGHT)
                    .typeface(CoCoinUtil.GetTypeface(), CoCoinUtil.GetTypeface())
                    .title(R.string.set_month_expense_dialog_title)
                    .inputType(InputType.TYPE_CLASS_NUMBER)
                    .positiveText(R.string.submit)
                    .alwaysCallInputCallback()
                    .input(null, null, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(final MaterialDialog dialog, final CharSequence input) {
                            setInput(input,dialog);
                            dialog.getActionButton(DialogAction.POSITIVE)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            SettingManager.getInstance()
                                                    .setMonthWarning(Integer.parseInt(input.toString()));
                                            updateSettingsToServer(UPDATE_MONTH_WARNING);
                                            SettingManager.getInstance()
                                                    .setMainViewRemindColorShouldChange(true);
                                            monthWarning.withNumber(SettingManager.getInstance()
                                                    .getMonthWarning()).setDuration(1000).start();
                                            dialog.dismiss();
                                        }
                                    });
                        }
                    }).show();
        }
    }

// Init the setting activity////////////////////////////////////////////////////////////////////////
    private void init() {
        back = (MaterialIconView)findViewById(R.id.icon_left);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        logo = (CircleImageView)findViewById(R.id.profile_image);
        logo.setOnClickListener(this);
        profileLayout = (MaterialRippleLayout)findViewById(R.id.profile_layout);
        userNameIcon = (MaterialIconView)findViewById(R.id.user_name_icon);
        userEmailIcon = (MaterialIconView)findViewById(R.id.user_email_icon);
        userName = (TextView)findViewById(R.id.user_name);
        userName.setTypeface(CoCoinUtil.typefaceLatoLight);
        userEmail = (TextView)findViewById(R.id.user_email);
        userEmail.setTypeface(CoCoinUtil.typefaceLatoLight);
        loginButton = (TextView)findViewById(R.id.login_button);
        loginButton.setTypeface(CoCoinUtil.typefaceLatoLight);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userOperator();
            }
        });
        expense = (RiseNumberTextView)findViewById(R.id.expense);
        expense.setTypeface(CoCoinUtil.typefaceLatoLight);
        records = (RiseNumberTextView)findViewById(R.id.records);
        records.setTypeface(CoCoinUtil.typefaceLatoLight);
        expenseTV = (TextView)findViewById(R.id.expense_text);
        expenseTV.setTypeface(CoCoinUtil.GetTypeface());
        recordsTV = (TextView)findViewById(R.id.records_text);
        recordsTV.setTypeface(CoCoinUtil.GetTypeface());

        expense.withNumber(RecordManager.SUM).setDuration(1500).start();
        records.withNumber(RecordManager.RECORDS.size()).setDuration(1500).start();

        monthLayout = (MaterialRippleLayout)findViewById(R.id.month_layout);
        monthIcon = (MaterialIconView)findViewById(R.id.month_limit_icon);
        monthMaxExpenseIcon = (MaterialIconView)findViewById(R.id.month_expense_icon);
        monthColorRemindIcon = (MaterialIconView)findViewById(R.id.month_color_icon);
        monthWarningIcon = (MaterialIconView)findViewById(R.id.warning_expense_icon);
        monthColorRemindTypeIcon = (MaterialIconView)findViewById(R.id.month_color_type_icon);
        monthColorRemindSelect = (MaterialIconView)findViewById(R.id.month_color_type);
        monthColorRemindSelect.setColor(SettingManager.getInstance().getRemindColor());
        monthForbiddenIcon = (MaterialIconView)findViewById(R.id.month_forbidden_icon);
        monthSB = (Switch)findViewById(R.id.month_limit_enable_button);
        monthSB.setOnCheckedChangeListener(this);
        monthColorRemindSB = (Switch)findViewById(R.id.month_color_remind_button);
        monthColorRemindSB.setOnCheckedChangeListener(this);
        monthForbiddenSB = (Switch)findViewById(R.id.month_forbidden_button);
        monthForbiddenSB.setOnCheckedChangeListener(this);
        monthMaxExpense = (RiseNumberTextView)findViewById(R.id.month_expense);
        setMaxExpense(monthMaxExpense);

// change the month limit///////////////////////////////////////////////////////////////////////////
        monthMaxExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                z2();
            }
        });
        monthWarning = (RiseNumberTextView)findViewById(R.id.warning_expense);
        monthWarning.setText(SettingManager.getInstance().getMonthWarning().toString());
        setMonthWar(monthWarning);
// change month warning/////////////////////////////////////////////////////////////////////////////
        monthWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                z3();
            }
        });
// change month remind color////////////////////////////////////////////////////////////////////////
        monthColorRemindSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingManager.getInstance()
                        .setMainViewRemindColorShouldChange(true);
                remindColorSelectDialog.show((AppCompatActivity) mContext);
            }
        });
        monthMaxExpense.setTypeface(CoCoinUtil.typefaceLatoLight);
        monthWarning.setTypeface(CoCoinUtil.typefaceLatoLight);
        monthLimitTV = (TextView)findViewById(R.id.month_limit_text);
        monthLimitTV.setTypeface(CoCoinUtil.GetTypeface());
        monthWarningTV = (TextView)findViewById(R.id.warning_expense_text);
        monthWarningTV.setTypeface(CoCoinUtil.GetTypeface());
        monthMaxExpenseTV = (TextView)findViewById(R.id.month_expense_text);
        monthMaxExpenseTV.setTypeface(CoCoinUtil.GetTypeface());
        monthColorRemindTV = (TextView)findViewById(R.id.month_color_remind_text);
        monthColorRemindTV.setTypeface(CoCoinUtil.GetTypeface());
        monthColorRemindTypeTV = (TextView)findViewById(R.id.month_color_type_text);
        monthColorRemindTypeTV.setTypeface(CoCoinUtil.GetTypeface());
        monthForbiddenTV = (TextView)findViewById(R.id.month_forbidden_text);
        monthForbiddenTV.setTypeface(CoCoinUtil.GetTypeface());

        accountBookNameLayout = (MaterialRippleLayout)findViewById(R.id.account_book_name_layout);
        accountBookNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAccountBookName();
            }
        });
        accountBookName = (TextView)findViewById(R.id.account_book_name);
        accountBookName.setTypeface(CoCoinUtil.GetTypeface());
        accountBookName.setText(SettingManager.getInstance().getAccountBookName());
        accountBookNameTV = (TextView)findViewById(R.id.account_book_name_text);
        accountBookNameTV.setTypeface(CoCoinUtil.GetTypeface());

        changePasswordLayout = (MaterialRippleLayout)findViewById(R.id.change_password_layout);
        changePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
        changePasswordTV = (TextView)findViewById(R.id.change_password_text);
        changePasswordTV.setTypeface(CoCoinUtil.GetTypeface());

        sortTagsLayout = (MaterialRippleLayout)findViewById(R.id.sort_tags_layout);
        sortTagsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortTags();
            }
        });
        sortTagsTV = (TextView)findViewById(R.id.sort_tags_text);
        sortTagsTV.setTypeface(CoCoinUtil.GetTypeface());

        showPictureLayout = (MaterialRippleLayout)findViewById(R.id.whether_show_picture_layout);
        showPictureIcon = (MaterialIconView)findViewById(R.id.whether_show_picture_icon);
        showPictureSB = (Switch)findViewById(R.id.whether_show_picture_button);
        showPictureSB.setOnCheckedChangeListener(this);
        showPictureTV = (TextView)findViewById(R.id.whether_show_picture_text);
        showPictureTV.setTypeface(CoCoinUtil.GetTypeface());

        hollowLayout = (MaterialRippleLayout)findViewById(R.id.whether_show_circle_layout);
        hollowIcon = (MaterialIconView)findViewById(R.id.whether_show_circle_icon);
        hollowSB = (Switch)findViewById(R.id.whether_show_circle_button);
        hollowSB.setOnCheckedChangeListener(this);
        hollowTV = (TextView)findViewById(R.id.whether_show_circle_text);
        hollowTV.setTypeface(CoCoinUtil.GetTypeface());

        updateLayout = (MaterialRippleLayout)findViewById(R.id.update_layout);
        updateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoCoinUtil.showToast(mContext, mContext.getResources().getString(R.string.checking_update), SuperToast.Background.BLUE);
                AppUpdateManager appUpdateManager = new AppUpdateManager(mContext);
                appUpdateManager.checkUpdateInfo(true);
            }
        });
        currentVersionTV = (TextView)findViewById(R.id.update_text);
        currentVersionTV.setTypeface(CoCoinUtil.GetTypeface());
        currentVersionTV.setText(mContext.getResources().getString(R.string.current_version) + CoCoinUtil.GetCurrentVersion());
        canBeUpdatedTV = (TextView)findViewById(R.id.update_tag);
        canBeUpdatedTV.setTypeface(CoCoinUtil.GetTypeface());
        setcanBe(canBeUpdatedTV);

        boolean loggenOn = SettingManager.getInstance().getLoggenOn();
        setLogenOn(loggenOn ,userName,userEmail,loginButton );
        setIconEnable(userNameIcon, loggenOn);
        setIconEnable(userEmailIcon, loggenOn);

        loadLogo();

        monthSB.setCheckedImmediately(SettingManager.getInstance().getIsMonthLimit());
        setMonthState();

        showPictureSB.setCheckedImmediately(SettingManager.getInstance().getShowPicture());
        setShowPictureState(SettingManager.getInstance().getShowPicture());

        hollowSB.setCheckedImmediately(SettingManager.getInstance().getIsHollow());
        setHollowState(SettingManager.getInstance().getIsHollow());
    }




    public void setMonthWar(RiseNumberTextView monthWarning) {
        if (SettingManager.getInstance().getIsMonthLimit()
                && SettingManager.getInstance().getIsColorRemind())
            monthWarning.withNumber(SettingManager.getInstance()
                    .getMonthWarning()).setDuration(1000).start();
    }

    public void setInput(CharSequence input,MaterialDialog dialog) {
        if (input.length() == 0) {
            dialog.setContent(mContext.getResources().getString(
                    R.string.set_warning_expense_dialog_title));
            dialog.getActionButton(DialogAction.POSITIVE)
                    .setEnabled(false);
        } else if (Integer.parseInt(input.toString()) < 100) {
            dialog.setContent("≥ 100");
            dialog.getActionButton(DialogAction.POSITIVE)
                    .setEnabled(false);
        } else if (Integer.parseInt(input.toString())
                > SettingManager.getInstance().getMonthLimit()) {
            dialog.setContent("≤ " + SettingManager.getInstance()
                    .getMonthLimit().toString());
            dialog.getActionButton(DialogAction.POSITIVE)
                    .setEnabled(false);
        } else {
            dialog.setContent(mContext.getResources().getString(
                    R.string.set_warning_expense_dialog_title));
            dialog.getActionButton(DialogAction.POSITIVE)
                    .setEnabled(true);
        }
    }
    public void setLogenOn(boolean loggenOn ,TextView userName, TextView userEmail, TextView loginButton ) {
        boolean loggenOn = SettingManager.getInstance().getLoggenOn();
        if (loggenOn) {
            // is logged on, set the user name and email
            userName.setText(SettingManager.getInstance().getUserName());
            userEmail.setText(SettingManager.getInstance().getUserEmail());
            loginButton.setText(mContext.getResources().getText(R.string.logout_button));
            loginButton.setBackgroundResource(R.drawable.button_logout);
        } else {
            userName.setText("");
            userEmail.setText("");
            loginButton.setText(getResourceString(R.string.login_button));
        }
    }

    public void setcanBe(TextView canBeUpdatedTV) {
        if (SettingManager.getInstance().getCanBeUpdated()) {
            canBeUpdatedTV.setVisibility(View.VISIBLE);
        } else {
            canBeUpdatedTV.setVisibility(View.GONE);
        }
    }
        public void setMaxExpense(RiseNumberTextView monthMaxExpense) {
            if (SettingManager.getInstance().getIsMonthLimit())
                monthMaxExpense.withNumber(SettingManager.getInstance()
                        .getMonthLimit()).setDuration(1000).start();
        }

        public void q1(){
            if (isMonthLimit && isMonthColorRemind) {
                monthColorRemindSelect.setEnabled(true);
                monthColorRemindSelect
                        .setColor(SettingManager.getInstance().getRemindColor());
                monthWarning.setEnabled(true);
                monthWarning.setTextColor(
                        ContextCompat.getColor(mContext, R.color.drawer_text));
            } else {
                monthColorRemindSelect.setEnabled(false);
                monthColorRemindSelect
                        .setColor(mContext.getResources().getColor(R.color.my_gray));
                monthWarning.setEnabled(false);
                monthWarning.setTextColor(
                        ContextCompat.getColor(mContext, R.color.my_gray));
            }
        }
// Set all states about month limit/////////////////////////////////////////////////////////////////
    private void setMonthState() {
        boolean isMonthLimit = SettingManager.getInstance().getIsMonthLimit();
        boolean isMonthColorRemind = SettingManager.getInstance().getIsColorRemind();
        boolean isForbidden = SettingManager.getInstance().getIsForbidden();

        setIconEnable(monthIcon, isMonthLimit);
        setIconEnable(monthMaxExpenseIcon, isMonthLimit);
        setTVEnable(monthMaxExpenseTV, isMonthLimit);
        setTVEnable(monthMaxExpense, isMonthLimit);
        setTVEnable(monthColorRemindTV, isMonthLimit);
        setTVEnable(monthColorRemindTypeTV, isMonthLimit );
        setTVEnable(monthColorRemindTypeTV, isMonthColorRemind);
        setTVEnable(monthWarningTV, isMonthLimit );
        setTVEnable(monthWarningTV,isMonthColorRemind);
        setTVEnable(monthForbiddenTV, isMonthLimit);
        monthMaxExpense.setText(SettingManager.getInstance().getMonthLimit() + "");

        setIconEnable(monthColorRemindIcon, isMonthLimit );
        setIconEnable(monthColorRemindIcon, isMonthColorRemind);
        setIconEnable(monthWarningIcon, isMonthLimit );
        setIconEnable(monthWarningIcon, isMonthColorRemind);
        setIconEnable(monthColorRemindTypeIcon, isMonthLimit );
        setIconEnable(monthColorRemindTypeIcon, isMonthColorRemind);
        setIconEnable(monthColorRemindSelect, isMonthLimit );
        setIconEnable(monthColorRemindSelect,isMonthColorRemind);
        q1();
        setIconEnable(monthForbiddenIcon, isMonthLimit);
        setIconEnable(monthForbiddenIcon, isForbidden);

        monthColorRemindSB.setEnabled(isMonthLimit);
        monthColorRemindSB.setCheckedImmediately(
                SettingManager.getInstance().getIsColorRemind());
        monthForbiddenSB.setEnabled(isMonthLimit);
        monthForbiddenSB.setCheckedImmediately(
                SettingManager.getInstance().getIsForbidden());
    }

    private void setShowPictureState(boolean isChecked) {
        setIconEnable(showPictureIcon, isChecked);
    }

    private void setHollowState(boolean isChecked) {
        setIconEnable(hollowIcon, isChecked);
    }

    private void setIconEnable(MaterialIconView icon, boolean enable) {
        if (enable) icon.setColor(mContext.getResources().getColor(R.color.my_blue));
        else icon.setColor(mContext.getResources().getColor(R.color.my_gray));
    }

    private void setTVEnable(TextView tv, boolean enable) {
        if (enable) tv.setTextColor(mContext.getResources().getColor(R.color.drawer_text));
        else tv.setTextColor(mContext.getResources().getColor(R.color.my_gray));
    }

// choose a color///////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onColorSelection(ColorChooserDialog dialog, int selectedColor) {
        monthColorRemindSelect.setColor(selectedColor);
        SettingManager.getInstance().setRemindColor(selectedColor);
        updateSettingsToServer(UPDATE_REMIND_COLOR);
        SettingManager.getInstance().setMainViewRemindColorShouldChange(true);
    }

    ColorChooserDialog remindColorSelectDialog =
            new ColorChooserDialog.Builder(this, R.string.set_remind_color_dialog_title)
                    .titleSub(R.string.set_remind_color_dialog_sub_title)
                    .preselect(SettingManager.getInstance().getRemindColor())
                    .doneButton(R.string.submit)
                    .cancelButton(R.string.cancel)
                    .backButton(R.string.back)
                    .customButton(R.string.custom)
                    .dynamicButtonColor(true)
                    .build();

// whether sync the settings from server////////////////////////////////////////////////////////////
    private void whetherSyncSettingsFromServer() {
        new MaterialDialog.Builder(this)
                .iconRes(R.drawable.cocoin_logo)
                .typeface(CoCoinUtil.GetTypeface(), CoCoinUtil.GetTypeface())
                .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                .title(R.string.sync_dialog_title)
                .forceStacking(true)
                .content(R.string.sync_dialog_content)
                .positiveText(R.string.sync_dialog_sync_to_local)
                .negativeText(R.string.sync_dialog_sync_to_server)
                .cancelable(false)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        if (which.equals(DialogAction.POSITIVE)) {
                            // sync to local
                            downloadLogoFromServer();
                            User user = getCurrentUser();
                            String tip = "";
                            boolean accountBookPasswordChanged = false;
                            if (!user.getAccountBookPassword().equals(SettingManager.getInstance().getPassword()))
                                accountBookPasswordChanged = true;

                            SettingManager.getInstance().setIsMonthLimit(user.getIsMonthLimit());
                            monthSB.setChecked(user.getIsMonthLimit());
                            SettingManager.getInstance().setMonthLimit(user.getMonthLimit());
                            if (SettingManager.getInstance().getIsMonthLimit())
                                monthMaxExpense.withNumber(SettingManager.getInstance()
                                        .getMonthLimit()).setDuration(1000).start();
                            SettingManager.getInstance().setIsColorRemind(user.getIsColorRemind());
                            monthColorRemindSB.setChecked(user.getIsColorRemind());
                            SettingManager.getInstance().setMonthWarning(user.getMonthWarning());
                            if (SettingManager.getInstance().getIsMonthLimit()
                                    && SettingManager.getInstance().getIsColorRemind())
                                monthWarning.withNumber(SettingManager.getInstance()
                                        .getMonthWarning()).setDuration(1000).start();
                            SettingManager.getInstance().setRemindColor(user.getRemindColor());
                            monthColorRemindTypeIcon.setColor(SettingManager.getInstance().getRemindColor());
                            SettingManager.getInstance().setIsForbidden(user.getIsForbidden());
                            monthForbiddenSB.setChecked(user.getIsForbidden());
                            SettingManager.getInstance().setAccountBookName(user.getAccountBookName());
                            accountBookName.setText(user.getAccountBookName());
                            SettingManager.getInstance().setPassword(user.getAccountBookPassword());
                            // Todo tag sort
                            SettingManager.getInstance().setShowPicture(user.getShowPicture());
                            showPictureSB.setChecked(user.getShowPicture());
                            SettingManager.getInstance().setIsHollow(user.getIsHollow());
                            hollowSB.setChecked(user.getIsHollow());
                            SettingManager.getInstance().setMainViewMonthExpenseShouldChange(true);
                            SettingManager.getInstance().setMainViewRemindColorShouldChange(true);
                            SettingManager.getInstance().setMainViewTitleShouldChange(true);
                            SettingManager.getInstance().setTodayViewMonthExpenseShouldChange(true);
                            SettingManager.getInstance().setTodayViewPieShouldChange(true);
                            SettingManager.getInstance().setTodayViewTitleShouldChange(true);
                            // SettingManager.getInstance().getMainActivityTagShouldChange();
                            if (accountBookPasswordChanged)
                                tip = "\n" + getString(R.string.your_current_account_book_password_is)
                                        + SettingManager.getInstance().getPassword();
                            new MaterialDialog.Builder(mContext)
                                    .typeface(CoCoinUtil.GetTypeface(), CoCoinUtil.GetTypeface())
                                    .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                                    .title(R.string.sync_to_local_successfully_dialog_title)
                                    .content(getString(R.string.sync_to_local_successfully_dialog_content) + tip)
                                    .positiveText(R.string.ok)
                                    .show();
                        } else if (which.equals(DialogAction.NEGATIVE)) {
                            // sync to server
                            uploadLogoToServer();
                            User user = getCurrentUser();
                            user.setIsMonthLimit(SettingManager.getInstance().getIsMonthLimit());
                            user.setMonthLimit(SettingManager.getInstance().getMonthLimit());
                            user.setIsColorRemind(SettingManager.getInstance().getIsColorRemind());
                            user.setMonthWarning(SettingManager.getInstance().getMonthWarning());
                            user.setRemindColor(SettingManager.getInstance().getRemindColor());
                            user.setIsForbidden(SettingManager.getInstance().getIsForbidden());
                            user.setAccountBookName(SettingManager.getInstance().getAccountBookName());
                            user.setAccountBookPassword(SettingManager.getInstance().getPassword());
                            // Todo tag sort
                            user.setShowPicture(SettingManager.getInstance().getShowPicture());
                            user.setIsHollow(SettingManager.getInstance().getIsHollow());
                            user.update(CoCoinApplication.getAppContext(),
                                    user.getObjectId(), new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    showToast(9, "");
                                }
                                @Override
                                public void onFailure(int code, String msg) {
                                    showToast(10, msg);
                                }
                            });
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void updateAllSettings() {
        updateSettingsToServer(0);
        updateSettingsToServer(1);
        updateSettingsToServer(2);
    }

    public void w1(){
        updateMontLimit(setting,currentUser);
        updateColorRemind(setting,currentUser);
    }

    public void w2(){
        updateMonthWarning(setting,currentUser);
        updateRwmindColor(setting,currentUser);
    }

    public void w3(){
        updateIsForbidden(setting,currentUser);
        updateAccountBookName(setting,currentUser);
    }

    public void w4(){
        updateAccountBookPassword(setting,currentUser);
        updateShowPicture(setting,currentUser);
    }

    public void w5(){
        updateIsHollow(setting,currentUser);
        updateLogoId(setting,currentUser);
    }

    public void w6(){
        @Override
        public void onSuccess() {
            Log.d("Saver", "Update " + setting + " successfully.");
            // the new account book name is updated to server successfully
            updateAccountcaseB(setting);
        }

        @Override
        public void onFailure(int code, String msg) {
            Log.d("Saver", "Update " + setting + " fail.");
            updateAccountcaseA(setting);
        }
    }


// update part of settings//////////////////////////////////////////////////////////////////////////
    private void updateSettingsToServer(final int setting) {
        User currentUser = getCurrentUser();
        checkUser(currentUser);
        w1();
        w2();
        w3();
        w4();
        w5();
        currentUser.update(CoCoinApplication.getAppContext(),
                currentUser.getObjectId(), new UpdateListener() {
                    w6();
                });
    }


    public void updateMontLimit(int setting, User currentUser){
        if(setting == UPDATE_IS_MONTH_LIMIT)
        currentUser.setIsMonthLimit(SettingManager.getInstance().getIsMonthLimit());
    }
    public void updateColorRemind(int setting, User currentUser){
        if(setting == UPDATE_IS_COLOR_REMIND)
        // is color remind
        currentUser.setIsColorRemind(SettingManager.getInstance().getIsColorRemind());
    }
    public void updateMonthWarning(int setting, User currentUser){
        if(setting == UPDATE_MONTH_WARNING)
        // month warning
        currentUser.setMonthWarning(SettingManager.getInstance().getMonthWarning());
    }
    public void updateRwmindColor(int setting, User currentUser){
        if(setting == UPDATE_REMIND_COLOR)
        // remind color
        currentUser.setRemindColor(SettingManager.getInstance().getRemindColor());
    }
    public void updateAccountBookName(int setting, User currentUser){
        if(setting == UPDATE_ACCOUNT_BOOK_NAME)
        // account book name
        currentUser.setAccountBookName(SettingManager.getInstance().getAccountBookName());
    }
    public void updateIsForbidden(int setting, User currentUser){
        if(setting == UPDATE_IS_FORBIDDEN)
            // is forbidden
            currentUser.setIsForbidden(SettingManager.getInstance().getIsForbidden());
    }
    public void updateAccountBookPassword(int setting, User currentUser){
        if(setting ==UPDATE_ACCOUNT_BOOK_PASSWORD)
        // account book password
        currentUser.setAccountBookPassword(SettingManager.getInstance().getPassword());
    }
    public void updateShowPicture(int setting, User currentUser){
        if(setting == UPDATE_SHOW_PICTURE)
        // show picture
        currentUser.setShowPicture(SettingManager.getInstance().getShowPicture());
    }
    public void updateIsHollow(int setting, User currentUser){
        if(setting == UPDATE_IS_HOLLOW)
        // is hollow
        currentUser.setIsHollow(SettingManager.getInstance().getIsHollow());
    }
    public void updateLogoId(int setting, User currentUser){
        if(setting == UPDATE_LOGO_ID)
        // has a logo which has been updated
        currentUser.setLogoObjectId(SettingManager.getInstance().getLogoObjectId());
    }


    public void updateAccountcaseA(int setting){
        // the new account book name is failed to updated to server
        if (setting == UPDATE_ACCOUNT_BOOK_NAME) showToast(1, "");
    }
    public void updateAccountcaseB(int setting){
        // the new account book name is failed to updated to server
        if (setting == UPDATE_ACCOUNT_BOOK_NAME) showToast(0, "");
    }
    public void checkUser(User currentUser){
        if (currentUser == null) {
            Log.d("Saver", "User hasn't log in.");
            return;
        }

    }

    private void syncUserInfo() {
        User user = BmobUser.getCurrentUser(CoCoinApplication.getAppContext(), User.class);

    }

// Get the current user/////////////////////////////////////////////////////////////////////////////
    private User getCurrentUser() {
        return BmobUser.getCurrentUser(CoCoinApplication.getAppContext(), User.class);
    }

// Get string///////////////////////////////////////////////////////////////////////////////////////
    private String getResourceString(int resourceId) {
        return CoCoinApplication.getAppContext().getResources().getString(resourceId);
    }

// activity finish//////////////////////////////////////////////////////////////////////////////////
    @Override
    public void finish() {

        SuperToast.cancelAllSuperToasts();

        super.finish();
    }

    public void a1(){
        Log.d("CoCoin", msg);
        SuperToast.cancelAllSuperToasts();
        SuperToast superToast = new SuperToast(mContext);
    }



// Show toast///////////////////////////////////////////////////////////////////////////////////////
    private void showToast(int toastType, String msg) {
        a1();

        a2();
        superToast.setTextColor(Color.parseColor("#ffffff"));
        superToast.setTextSize(SuperToast.TextSize.SMALL);

        String tip = "";
        // the new account book name is updated to server successfully
        setSupportCase0(toastType,superToast);
        // the new account book name is failed to updated to server
        setSupportCase1(toastType,superToast);
        // the new account book name is changed successfully
        setSupportCase2(toastType,superToast);
        // the new account book name is failed to change
        setSupportCase3(toastType,superToast);
        // register successfully
        setSupportCase4(toastType,superToast,tip);
        // register failed
        setSupportCase5(toastType,superToast,tip,msg);
        // login successfully
        setSupportCase6(toastType,superToast,tip,msg);
        // login failed
        setSupportCase7(toastType,superToast,tip,msg);
        // log out successfully
        setSupportCase8(toastType,superToast);
        // sync settings successfully
        setSupportCase9(toastType,superToast);
        // sync settings failed
        setSupportCase10(toastType,superToast,tip);

        superToast.getTextView().setTypeface(CoCoinUtil.GetTypeface());

        superToast.show();
    }
    public void setSupportCase0(int toastType,SuperToast superToast){
        if( toastType ==0){
            // the new account book name is updated to server successfully
            superToast.setText(CoCoinApplication.getAppContext().getResources().getString(
                    R.string.change_and_update_account_book_name_successfully));
            superToast.setBackground(SuperToast.Background.BLUE);
        }
    }
    public void setSupportCase1(int toastType,SuperToast superToast){
        if( toastType ==1){
            // the new account book name is failed to updated to server
            superToast.setText(CoCoinApplication.getAppContext().getResources().getString(
                    R.string.change_and_update_account_book_name_fail));
            superToast.setBackground(SuperToast.Background.RED);
        }
    }
    public void setSupportCase2(int toastType,SuperToast superToast){
        if( toastType ==2){
            // the new account book name is changed successfully
            superToast.setText(CoCoinApplication.getAppContext().getResources().getString(
                    R.string.change_account_book_name_successfully));
            superToast.setBackground(SuperToast.Background.BLUE);
        }
    }
    public void setSupportCase3(int toastType,SuperToast superToast){
        if( toastType ==3){
            // the new account book name is failed to change
            superToast.setText(CoCoinApplication.getAppContext().getResources().getString(
                    R.string.change_account_book_name_fail));
            superToast.setBackground(SuperToast.Background.RED);
        }
    }
    public void setSupportCase4(int toastType,SuperToast superToast,String tip){
        if( toastType ==4){
            // register successfully
            tip = msg;
            superToast.setText(getResourceString(R.string.register_successfully) + tip);
            superToast.setBackground(SuperToast.Background.BLUE);
        }
    }
    public void setSupportCase5(int toastType,SuperToast superToast,String tip,String msg){
        if( toastType ==5){
            // register failed
            tip = getResourceString(R.string.network_disconnection);
            if (msg.charAt(1) == 's') tip = getResourceString(R.string.user_name_exist);
            if (msg.charAt(0) == 'e') tip = getResourceString(R.string.user_email_exist);
            if (msg.charAt(1) == 'n') tip = getResourceString(R.string.user_mobile_exist);
            superToast.setText(getResourceString(R.string.register_fail) + tip);
            superToast.setBackground(SuperToast.Background.RED);
        }
    }
    public void setSupportCase6(int toastType,SuperToast superToast,String tip,String msg){
        if( toastType ==6){
            // login successfully
            tip = msg;
            superToast.setText(getResourceString(R.string.login_successfully) + tip);
            superToast.setBackground(SuperToast.Background.BLUE);
        }
    }
    public void setSupportCase7(int toastType,SuperToast superToast,String tip,String msg){
        if( toastType ==7){
            // login failed
            tip = getResourceString(R.string.network_disconnection);
            if (msg.charAt(0) == 'u') tip = getResourceString(R.string.user_name_or_password_incorrect);
            if (msg.charAt(1) == 'n') tip = getResourceString(R.string.user_mobile_exist);
            superToast.setText(getResourceString(R.string.login_fail) + tip);
            superToast.setBackground(SuperToast.Background.RED);
        }
    }
    public void setSupportCase8(int toastType,SuperToast superToast){
        if( toastType ==8){
            // log out successfully
            superToast.setText(getResourceString(R.string.log_out_successfully));
            superToast.setBackground(SuperToast.Background.BLUE);
        }
    }
    public void setSupportCase9(int toastType,SuperToast superToast){
        if( toastType ==9){
            // sync settings successfully
            superToast.setText(getResourceString(R.string.sync_to_server_successfully));
            superToast.setBackground(SuperToast.Background.BLUE);
        }
    }
    public void setSupportCase10(int toastType,SuperToast superToast,String tip){
        if( toastType ==10){
            // sync settings failed
            tip = getResourceString(R.string.network_disconnection);
            superToast.setText(getResourceString(R.string.sync_to_server_failed) + tip);
            superToast.setBackground(SuperToast.Background.RED);
        }
    }

}
