package com.asimodabas.src_team.ui.fragment

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.asimodabas.Constants.IMAGE_NAME_NEW
import com.asimodabas.src_team.R
import com.asimodabas.src_team.databinding.DeleteAccountConfirmDialogBinding
import com.asimodabas.src_team.databinding.FragmentEditBinding
import com.asimodabas.src_team.model.SrcProfile
import com.asimodabas.src_team.ui.activity.MainActivity
import com.asimodabas.src_team.viewmodel.EditViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.util.*

class EditFragment : Fragment() {


    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: EditViewModel
    private var userProfileInfo: SrcProfile? = null
    private var selectedBitmap: Bitmap? = null
    private var selectedUri: Uri? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private val neededRuntimePermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        val view = binding.root
        registerLauncher()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[EditViewModel::class.java]

        viewModel.getProfileInfo()
        getProfileInfo()

        binding.uploadImageView.setOnClickListener {
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

        binding.uploadButton.setOnClickListener {
            if (dataControl()) {
                val newName = binding.editTextTextPersonName.text.toString()
                val newSurname = binding.editTextTextPersonName7.text.toString()
                if (selectedBitmap != null) {
                    val smallBitmap = makeSmallerBitmap(selectedBitmap!!, 400)
                    viewModel.updateProfile(
                        userProfileInfo!!,
                        newName,
                        newSurname,
                        getImageUri(requireContext(), smallBitmap)
                    )
                    observeUpdateProfile()
                } else {
                    viewModel.updateProfile(
                        userProfileInfo!!, newName, newSurname, null
                    )
                    observeUpdateProfile()
                }
            } else {
                Toast.makeText(requireContext(), R.string.fill_in_the_blanks, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.deleteAccountButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(R.string.delete_my_account)
            builder.setMessage(R.string.delete_account_confirm)
            builder.setCancelable(true)
            builder.setNegativeButton(R.string.no) { _, _ ->
            }
            builder.setPositiveButton(R.string.yes) { _, _ ->
                val email = userProfileInfo!!.email
                val dialog = Dialog(requireContext())
                val dialogPasswordBinding = DeleteAccountConfirmDialogBinding.inflate(
                    LayoutInflater.from(requireContext())
                )
                dialog.setContentView(dialogPasswordBinding.root)
                dialogPasswordBinding.buttonConfirm.setOnClickListener {
                    if (dialogPasswordBinding.editTextPass.text.isNotEmpty()) {
                        dialog.cancel()
                        val password = dialogPasswordBinding.editTextPass.text.toString()
                        val credential = EmailAuthProvider
                            .getCredential(email, password)
                        val user = Firebase.auth.currentUser!!
                        user.reauthenticate(credential).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                viewModel.deleteAccount()
                                viewModel.deleteAccountAnimation.observe(
                                    viewLifecycleOwner
                                ) {

                                }
                                viewModel.deleteAccountError.observe(viewLifecycleOwner) { error ->
                                    error?.let {
                                        if (it) {
                                            Toast.makeText(
                                                requireContext(),
                                                R.string.try_again_later,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                                viewModel.deleteAccountConfirmation.observe(
                                    viewLifecycleOwner
                                ) { confirm ->
                                    confirm?.let {
                                        if (it) {
                                            Toast.makeText(
                                                requireContext(),
                                                R.string.deletion_success,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            val intent = Intent(
                                                requireActivity(),
                                                MainActivity::class.java
                                            )
                                            activity?.let { activity ->
                                                activity.startActivity(intent)
                                                activity.finish()
                                            }
                                        }
                                    }
                                }
                                viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
                                    error?.let {
                                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                            } else {
                                println(task.exception)
                                Toast.makeText(
                                    requireContext(),
                                    R.string.try_again_later,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            R.string.fill_in_the_blanks,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                dialog.show()
            }
            builder.show()
        }

        binding.updatePasswordButton.setOnClickListener {
            val action =
                EditFragmentDirections.actionEditFragmentToUpdatePasswordFragment(
                    userProfileInfo!!.email
                )
            findNavController().navigate(action)
        }
    }

    private fun observeUpdateProfile() {
        viewModel.changesSaved.observe(viewLifecycleOwner) { isSaved ->
            isSaved?.let {
                if (it) {
                    Toast.makeText(requireContext(), R.string.changes_saved, Toast.LENGTH_SHORT)
                        .show()
                    val action =
                        EditFragmentDirections.actionEditFragmentToProfileFragment()
                    findNavController().navigate(action)
                }
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun getProfileInfo() {

        viewModel.dataConfirmation.observe(viewLifecycleOwner) { dataConfirm ->
            dataConfirm?.let { data ->
                if (data) {
                    userProfileInfo = viewModel.userInfo
                    binding.editTextTextPersonName.setText(userProfileInfo!!.name)
                    binding.editTextTextPersonName7.setText(userProfileInfo!!.surname)
                    if (userProfileInfo!!.profileImage != null) {
                        Picasso.get()
                            .load(userProfileInfo!!.profileImage)
                            .placeholder(R.drawable.person_high_resolution)
                            .error(R.drawable.error)
                            .into(binding.uploadImageView)
                    } else {
                        binding.uploadImageView.setImageDrawable(
                            ActivityCompat.getDrawable(
                                requireContext(),
                                R.drawable.src_logo
                            )
                        )
                    }

                } else {
                    Toast.makeText(requireContext(), "Hata", Toast.LENGTH_SHORT).show()
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
                                    binding.uploadImageView.setImageBitmap(selectedBitmap)
                                } else {
                                    selectedBitmap = MediaStore.Images.Media.getBitmap(
                                        requireActivity().contentResolver,
                                        imageData
                                    )
                                    binding.uploadImageView.setImageBitmap(selectedBitmap)
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
                        val galeriIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        activityResultLauncher.launch(galeriIntent)
                    }
                }
            }
    }

    private fun makeSmallerBitmap(image: Bitmap, maximumSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio: Double = width.toDouble() / height.toDouble()

        if (bitmapRatio > 1) {
            // Landscape
            width = maximumSize
            val scaleHeight = width / bitmapRatio
            height = scaleHeight.toInt()
        } else {
            // Portrait
            height = maximumSize
            val scaleWidth = height * bitmapRatio
            width = scaleWidth.toInt()
        }

        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(
                inContext.contentResolver,
                inImage,
                IMAGE_NAME_NEW,
                null
            )
        return Uri.parse(path)
    }

    private fun dataControl(): Boolean = binding.editTextTextPersonName.text.isNotEmpty()
            && binding.editTextTextPersonName7.text.isNotEmpty()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}