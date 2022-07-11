package com.asimodabas.src_team.ui.fragment


import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.asimodabas.Constants.IMAGE_NAME
import com.asimodabas.src_team.R
import com.asimodabas.src_team.databinding.FragmentCreateBinding
import com.asimodabas.src_team.viewmodel.CreateViewModel
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.util.*

class CreateFragment : Fragment() {

    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CreateViewModel
    private var selectedBitmap: Bitmap? = null
    private var selectedUri: Uri? = null
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private val neededRuntimePermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        val view = binding.root

        registerLauncher()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[CreateViewModel::class.java]

        binding.imageView4.setOnClickListener {
            if ((ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                        + ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ))
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    Snackbar.make(
                        it,
                        R.string.gallery_permission,
                        Snackbar.LENGTH_LONG
                    ).setAction(R.string.give_permission) {
                        permissionLauncher.launch(neededRuntimePermissions)
                    }.show()
                } else {
                    permissionLauncher.launch(neededRuntimePermissions)
                }
            } else {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(galleryIntent)
            }
        }

        binding.fcreateButton.setOnClickListener {
            if (binding.imageView4.drawable != null) {
                if (selectedBitmap != null) {
                    val smallBitmap = makeSmallerBitmap(selectedBitmap!!, 400)
                    firebaseDataSave(getImageUri(requireContext(), smallBitmap))
                    goSecond()
                } else {
                    firebaseDataSave(null)
                    goSecond()
                }
            } else {
                Toast.makeText(requireContext(), R.string.select_picture, Toast.LENGTH_SHORT).show()
            }
        }
        binding.loginTextView.setOnClickListener {
            findNavController().navigate(R.id.action_createFragment_to_loginFragment)
        }
    }

    private fun goSecond() {
        Handler().postDelayed({
            findNavController().navigate(R.id.action_createFragment_to_secondFragment)
        }, 4000)
    }

    private fun firebaseDataSave(selectedImage: Uri?) {
        if (dataControl()) {
            val name = binding.nameEditText.text.toString()
            val surname = binding.surnameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (password != null) {
                viewModel.registerToApp(email, password, name, surname, selectedImage)
                observeData()
            } else {
                Toast.makeText(requireContext(), R.string.password_must_match, Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(requireContext(), R.string.fill_in_the_blanks, Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeData() {

        viewModel.dataConfirmation.observe(viewLifecycleOwner) { dataConfirm ->
            dataConfirm?.let { confirm ->
                if (confirm) {
                    activity?.let {
                        Toast.makeText(
                            requireContext(),
                            "${resources.getString(R.string.welcome)} ${binding.nameEditText.text}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        val imageData = intentFromResult.data
                        selectedUri = imageData
                        if (imageData != null) {
                            try {
                                if (Build.VERSION.SDK_INT >= 28) {
                                    val source = ImageDecoder.createSource(
                                        requireActivity().contentResolver,
                                        imageData
                                    )
                                    selectedBitmap = ImageDecoder.decodeBitmap(source)
                                    binding.imageView4.setImageBitmap(selectedBitmap)
                                } else {
                                    selectedBitmap = MediaStore.Images.Media.getBitmap(
                                        requireActivity().contentResolver,
                                        imageData
                                    )
                                    binding.imageView4.setImageBitmap(selectedBitmap)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.entries.forEach {
                    if (it.value && it.key == Manifest.permission.READ_EXTERNAL_STORAGE) {
                        val galleryIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        activityResultLauncher.launch(galleryIntent)
                    }
                }
            }
    }

    private fun makeSmallerBitmap(image: Bitmap, maximumSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio: Double = width.toDouble() / height.toDouble()

        if (bitmapRatio > 1) {
            width = maximumSize
            val scaleHeight = width / bitmapRatio
            height = scaleHeight.toInt()
        } else {
            height = maximumSize
            val scaleWidth = height * bitmapRatio
            width = scaleWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val path =
            MediaStore.Images.Media.insertImage(
                inContext.contentResolver,
                inImage,
                IMAGE_NAME,
                null
            )
        return Uri.parse(path)
    }

    private fun dataControl(): Boolean =
        binding.nameEditText.text.isNotEmpty() &&
                binding.surnameEditText.text.isNotEmpty() &&
                binding.emailEditText.text.isNotEmpty() &&
                binding.passwordEditText.text.isNotEmpty()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}