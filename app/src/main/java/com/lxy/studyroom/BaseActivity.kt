package com.lxy.studyroom

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.*
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cc.shinichi.library.ImagePreview
import com.github.gzuliyujiang.wheelpicker.AddressPicker
import com.github.gzuliyujiang.wheelpicker.annotation.AddressMode
import com.github.gzuliyujiang.wheelpicker.entity.CityEntity
import com.github.gzuliyujiang.wheelpicker.entity.CountyEntity
import com.github.gzuliyujiang.wheelpicker.entity.ProvinceEntity
import com.jakewharton.rxbinding3.widget.textChanges
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.style.PictureSelectorStyle
import com.luck.picture.lib.style.PictureWindowAnimationStyle
import com.lxy.studyroom.StudyRoomApplication.Companion.context
import com.lxy.studyroom.util.LogUtil
import com.lxy.studyroom.widget.view.GlideEngine
import com.permissionx.guolindev.PermissionX
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropImageEngine
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_edit_user_info.*
import kotlinx.android.synthetic.main.version_update_info.*
import java.io.File
import java.util.concurrent.TimeUnit


open class BaseActivity : AppCompatActivity() {

    companion object {
        const val TAG = "BaseActivity"

        const val BACK_MSG = "再按一次返回键退出程序"
    }

    private val pathPicDownload = context.getExternalFilesDir("")!!.absolutePath + "/picture"

    private lateinit var baseDialog: AlertDialog

    private var mCompositeDisposable: CompositeDisposable? = null

    ///////////////////////////生命周期

    /**
     * 在Activity第一次被创建的时候
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCollector.addActivity(this)

        //系统自带的标题栏隐藏掉
        supportActionBar?.hide()
        //initStatusBar()
        //        StatusBarUtil.immersiveStatusBar(this, Color.TRANSPARENT)
//             ImmersionBar.with(this)
//            .fitsSystemWindows(true)
//            .statusBarDarkFont(true)
//            .navigationBarColor(R.color.white)
//            .navigationBarDarkIcon(true)
//            .init()
        //只能竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        LogUtil.e(TAG, "onCreate ${javaClass.simpleName}")

    }

    /**
     * 在Activity由不可见变为可见的时候
     */
    override fun onStart() {
        super.onStart()
        LogUtil.e(TAG, "onStart ${javaClass.simpleName}" )
    }

    /**
     * 这个方法在Activity准备好和用户进行交互的时候调用。此时的Activity一定位于返回栈的栈顶，并且处于运行状态。
     */
    override fun onResume() {
        super.onResume()
        LogUtil.e(TAG, "onResume ${javaClass.simpleName}")
    }

    /**
     * 这个方法在系统准备去启动或者恢复另一个Activity的时候调用。我们通常
     * 会在这个方法中将一些消耗CPU的资源释放掉，以及保存一些关键数据，但这个方法的执
     * 行速度一定要快，不然会影响到新的栈顶Activity的使用。
     */
    override fun onPause() {
        super.onPause()
        LogUtil.e(TAG, "onPause ${javaClass.simpleName}")
    }

    /**
     * 这个方法在Activity完全不可见的时候调用。它和onPause()方法的主要区
     * 别在于，如果启动的新Activity是一个对话框式的Activity，那么onPause()方法会得到执
     * 行，而onStop()方法并不会执行。
     */
    override fun onStop() {
        super.onStop()
        LogUtil.e(TAG, "onStop ${javaClass.simpleName}")
    }

    /**
     * 这个方法在Activity被销毁之前调用，之后Activity的状态将变为销毁状态。
     */
    override fun onDestroy() {
        super.onDestroy()
        clearDisposable()
        LogUtil.e(TAG, "onDestroy ${javaClass.simpleName}")
        ActivityCollector.removeActivity(this)
    }

    /**
     * 这个方法在Activity由停止状态变为运行状态之前调用，也就是Activity被重新启动了。
     */
    override fun onRestart() {
        super.onRestart()
        LogUtil.e(TAG, "onRestart ${javaClass.simpleName}")
    }

    ///////////////////////////生命周期结束

    /**
     * 初始化状态栏相关，
     * PS: 设置全屏需要在调用super.onCreate(arg0);之前设置setIsFullScreen(true);否则在Android 6.0下非全屏的activity会出错;
     * SDK19：可以设置状态栏透明，但是半透明的SYSTEM_BAR_BACKGROUNDS会不好看；
     * SDK21：可以设置状态栏颜色，并且可以清除SYSTEM_BAR_BACKGROUNDS，但是不能设置状态栏字体颜色（默认的白色字体在浅色背景下看不清楚）；
     * SDK23：可以设置状态栏为浅色（SYSTEM_UI_FLAG_LIGHT_STATUS_BAR），字体就回反转为黑色。
     * 为兼容目前效果，仅在SDK23才显示沉浸式。
     */
    protected fun initStatusBar() {
        val win: Window = window
        if (isFullScreen()) {
            win.requestFeature(Window.FEATURE_NO_TITLE)
            win.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN) //去掉信息栏
            win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) // 保持屏幕高亮
        } else {
            //KITKAT也能满足，只是SYSTEM_UI_FLAG_LIGHT_STATUS_BAR（状态栏字体颜色反转）只有在6.0才有效
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) //透明状态栏
                // 状态栏字体设置为深色，SYSTEM_UI_FLAG_LIGHT_STATUS_BAR 为SDK23增加
                win.decorView
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)

                // 部分机型的statusbar会有半透明的黑色背景
                win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                win.statusBarColor = Color.TRANSPARENT // SDK21
                //isStatusBarTranslate = true
            }
        }
    }

    /**
     * 是否全屏
     */
    private fun isFullScreen(): Boolean {
        val flg = window.attributes.flags
        var flag = false
        if (flg and WindowManager.LayoutParams.FLAG_FULLSCREEN == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
            flag = true
        }
        return flag
    }

    /**
     * 添加订阅
     */
    private fun addDisposable(mDisposable: Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable?.add(mDisposable)
    }

    /**
     * 取消所有订阅
     */
    private fun clearDisposable(){
        mCompositeDisposable?.clear()
    }

    /**
     * 监听textView
     */
    protected fun textChanges(view: TextView,timeout: Long,onNext: Consumer<String>){
        addDisposable(view.textChanges()
            .skipInitialValue()
            .debounce(timeout, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { obj: CharSequence -> obj.toString() }
            .subscribe(
                onNext
            ) { throwable: Throwable ->
                LogUtil.e(
                    "ccc",
                    "BaseActivity.accept: " + throwable.message
                )
            })
    }

    ////加载中

    protected fun showDianDianLoading(){
        baseDialog = AlertDialog.Builder(this@BaseActivity).create().apply {
            setCancelable(true)
            setCanceledOnTouchOutside(false)
        }
        val view = View.inflate(this@BaseActivity,R.layout.dialog_diandian_loading,null)
        baseDialog.setView(view)
        baseDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        baseDialog.show()

    }

    protected fun destroyDianDianLoading(){
        if (::baseDialog.isInitialized && baseDialog.window != null) {
            baseDialog.window!!.decorView.post { baseDialog.dismiss() }
        }
    }
    ////加载中结束

    //#FF3C3C红   #258EEC蓝 修改字体颜色
    protected fun setTextColor(view: TextView,color: Int){
        view.setTextColor(resources.getColor(color))
    }

    /**
     * 将来某个时间处理某个函数
     */
    protected fun getHandler(func: () -> Unit) : Handler {
        return object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                func()
            }
        }
    }

    /**
     * 图片选择器，高阶函数真他妈好用
     */
    protected fun pictureSelector(handleResult: (file: File) -> Unit){
        PictureSelector.create(this@BaseActivity)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine.createGlideEngine())
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setMaxSelectNum(1)
            .setSelectorUIStyle(getSelectorStyle())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>) {
                    val realPath = result[0].realPath
                    val file = File(realPath)
                    handleResult(file)
                }
                override fun onCancel() {}
            })
    }


    /**
     * 图片选择器(裁剪)
     */
    protected fun pictureSelectorCrop(handleResult: (file: File) -> Unit,x: Float,y:Float,circle: Boolean){
        PictureSelector.create(this@BaseActivity)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine.createGlideEngine())
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setMaxSelectNum(1)
            .setSelectorUIStyle(getSelectorStyle())

            .setCropEngine { fragment, srcUri, destinationUri,dataSource, requestCode ->
                val uCrop = UCrop.of(srcUri, destinationUri, dataSource).apply {
                    withOptions(buildOptions(x, y,circle))
                    setImageEngine(object : UCropImageEngine {
                        override fun loadImage(context: Context?, url: String?, imageView: ImageView?) {}
                        override fun loadImage(
                            context: Context?,
                            url: Uri?,
                            maxWidth: Int,
                            maxHeight: Int,
                            call: UCropImageEngine.OnCallbackListener<Bitmap>?) {}
                    })

                }
                uCrop.start(fragment.requireContext(),fragment,requestCode)
            }

            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>) {
                    val realPath = result[0].cutPath
                    val file = File(realPath)
                    handleResult(file)
                }
                override fun onCancel() {}
            })
    }

    /**
     * 裁剪选项
     */
    private fun buildOptions(x: Float, y:Float,circle: Boolean): UCrop.Options{
        return UCrop.Options().apply {
            setHideBottomControls(true)
            setFreeStyleCropEnabled(false)
            setShowCropFrame(true)
            setShowCropGrid(true)
            setCircleDimmedLayer(circle)
            withAspectRatio(x,y)
            setCropOutputPathDir(getSandboxPath())
            isCropDragSmoothToCenter(false)
            setSkipCropMimeType(PictureMimeType.ofGIF(), PictureMimeType.ofWEBP())
            isForbidCropGifWebp(true)
            isForbidSkipMultipleCrop(true)
            setMaxScaleMultiplier(100F)
            setStatusBarColor(ContextCompat.getColor(baseContext,R.color.ps_color_grey))
            setToolbarColor(ContextCompat.getColor(baseContext, R.color.ps_color_grey))
            setToolbarWidgetColor(ContextCompat.getColor(baseContext, R.color.white))

        }
    }

    private fun getSandboxPath(): String {
        val externalFilesDir: File = this@BaseActivity.getExternalFilesDir("")!!
        val customFile = File(externalFilesDir.absolutePath, "Sandbox")
        if (!customFile.exists()) {
            customFile.mkdirs()
        }
        return customFile.absolutePath + File.separator
    }

    /**
     * 省市县三级联动地址选择器
     */
    protected fun addressSelector(handleAddress: (province: ProvinceEntity, city: CityEntity, county: CountyEntity) -> Unit){
        val picker = AddressPicker(this@BaseActivity).apply {
            setAddressMode(AddressMode.PROVINCE_CITY_COUNTY)
            setDefaultValue("湖北省", "随州市", "广水市")
            setOnAddressPickedListener { province, city, county ->
                handleAddress(province, city, county)
            }
        }
        picker.show()
    }

    /**
     * 图片选择器样式
     */
    private fun getSelectorStyle(): PictureSelectorStyle {
        //向上弹出动画
        val animationStyle = PictureWindowAnimationStyle().apply {
            setActivityEnterAnimation(R.anim.ps_anim_up_in)
            setActivityExitAnimation(R.anim.ps_anim_down_out)
        }

        val selectorStyle = PictureSelectorStyle().apply{
            windowAnimationStyle = animationStyle
        }
        return selectorStyle
    }

    /**
     * 获取存储权限
     */
    protected fun getPermission(){
        val requestList = ArrayList<String>()
        requestList.add(Manifest.permission.CAMERA)
        requestList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestList.add(Manifest.permission.READ_MEDIA_IMAGES)
            requestList.add(Manifest.permission.READ_MEDIA_AUDIO)
            requestList.add(Manifest.permission.READ_MEDIA_VIDEO)
            requestList.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        }
        val message = "团团云自习需要您同意以下权限才能正常使用"
        PermissionX.init(this@BaseActivity)
            .permissions(requestList)
            .explainReasonBeforeRequest()
//            .onExplainRequestReason { scope, deniedList ->
//
//                scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny")
//            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, message, "Allow", "Deny")
            }
            .request { allGranted, _, deniedList ->
                if (!allGranted) {
                    LogUtil.e("getPermission","拒绝了如下权限：$deniedList")
                }
            }
    }

    protected fun getImagePreview(url: String){
        ImagePreview.instance
            // 上下文，必须是activity，不需要担心内存泄漏，本框架已经处理好；
            .setContext(this@BaseActivity)
            // 设置从第几张开始看（索引从0开始）
            .setIndex(0)
            // 只有一张图片的情况，可以直接传入这张图片的url
            .setImage(url)
            // 保存的文件夹名称，会在Picture目录进行文件夹的新建。比如："BigImageView"，会在Picture目录新建BigImageView文件夹)
            //.setFolderName(pathPicDownload)
            //开启下拉图片退出
            .setEnableDragClose(true)
            // 开启预览
            .start()
    }

}