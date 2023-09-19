package com.jiangdg.demo

import android.annotation.SuppressLint
import android.media.MediaScannerConnection
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.TimeUtils
import com.jiangdg.ausbc.MultiCameraClient
import com.jiangdg.ausbc.base.CameraFragment
import com.jiangdg.ausbc.callback.ICameraStateCallBack
import com.jiangdg.ausbc.callback.ICaptureCallBack
import com.jiangdg.ausbc.utils.ToastUtils
import com.jiangdg.ausbc.widget.AspectRatioTextureView
import com.jiangdg.ausbc.widget.IAspectRatio
import com.jiangdg.demo.databinding.FragmentMyDemoBinding
import java.io.File


class MyDemoFragment : CameraFragment() {

    companion object {
        private const val TAG = "MyDemoFragment"
    }

    var mBinding: FragmentMyDemoBinding? = null

    override fun getCameraView(): IAspectRatio? {
        return AspectRatioTextureView(requireContext())
    }

    override fun getCameraViewContainer(): ViewGroup? {
        return mBinding?.cameraViewContainer
    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentMyDemoBinding.inflate(inflater, container, false)
        initMyView()
        return mBinding?.root
    }

    private fun initMyView() {
        mBinding?.ivCaptureImage?.setOnClickListener {

            val path = PathUtils.getExternalDcimPath() + File.separator +
                    "Image_${TimeUtils.getNowString(TimeUtils.getSafeDateFormat("yyyy_MM_dd_HH_mm_sss"))}.jpg"

            captureImage(
                object : ICaptureCallBack {
                    override fun onBegin() {
                        Log.d(TAG, "onBegin: ")
                    }

                    override fun onError(error: String?) {
                        Log.e(TAG, "onError: $error")
                        Toast.makeText(requireContext(), "error:$error", Toast.LENGTH_SHORT).show()
                    }

                    override fun onComplete(path: String?) {
                        Log.d(TAG, "onComplete: path:$path")

                        MediaScannerConnection.scanFile(
                            requireContext(),
                            arrayOf(path),
                            null,
                            null
                        )
                        Toast.makeText(requireContext(), "save image succeed", Toast.LENGTH_SHORT)
                            .show()

                    }
                }, path
            )
        }


        mBinding?.ivSettingResolution?.setOnClickListener {

            showResolutionDialog()
        }
    }


    @SuppressLint("CheckResult")
    private fun showResolutionDialog() {
        getAllPreviewSizes().let { previewSizes ->
            if (previewSizes.isNullOrEmpty()) {
                ToastUtils.show("Get camera preview size failed")
                return
            }
            val list = arrayListOf<String>()
            var selectedIndex: Int = -1
            for (index in (0 until previewSizes.size)) {
                val w = previewSizes[index].width
                val h = previewSizes[index].height
                getCurrentPreviewSize()?.apply {
                    if (width == w && height == h) {
                        selectedIndex = index
                    }
                }
                list.add("$w x $h")
            }
            MaterialDialog(requireContext()).show {
                listItemsSingleChoice(
                    items = list,
                    initialSelection = selectedIndex
                ) { dialog, index, text ->
                    if (selectedIndex == index) {
                        return@listItemsSingleChoice
                    }
                    updateResolution(previewSizes[index].width, previewSizes[index].height)
                }
            }
        }
    }

    override fun onCameraState(
        self: MultiCameraClient.ICamera,
        code: ICameraStateCallBack.State,
        msg: String?
    ) {
        Log.d(TAG, "onCameraState: ${code.name}")
    }
}