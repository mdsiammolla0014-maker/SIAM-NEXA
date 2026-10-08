package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.AuthResult
import com.example.ui.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var isLoginTab by remember { mutableStateOf(true) }

    // Login fields (Email OR Username)
    var loginIdentifier by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }

    // Signup fields (Name, Username, Email, Password, Phone, Referral)
    var signupName by remember { mutableStateOf("") }
    var signupUsername by remember { mutableStateOf("") }
    var signupEmail by remember { mutableStateOf("") }
    var signupPassword by remember { mutableStateOf("") }
    var signupPhone by remember { mutableStateOf("") }
    var signupReferralCode by remember { mutableStateOf("") }
    var signupPasswordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // App Brand Logo & Name
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 6.dp,
                modifier = Modifier.size(84.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.siam_nexa_icon),
                    contentDescription = "SIAM NEXA Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "SIAM NEXA",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
            )

            Text(
                text = "স্মার্ট মাইক্রো-জব ও অনলাইন আর্নিং প্ল্যাটফর্ম",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Tab Selector Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    TabButton(
                        title = "লগইন (Login)",
                        isSelected = isLoginTab,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            isLoginTab = true
                            errorMessage = null
                        }
                    )
                    TabButton(
                        title = "রেজিস্ট্রেশন (Sign Up)",
                        isSelected = !isLoginTab,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            isLoginTab = false
                            errorMessage = null
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Error banner
            AnimatedVisibility(visible = errorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        )
                    }
                }
            }

            // Form Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    if (isLoginTab) {
                        // --- LOGIN FORM ---
                        Text(
                            text = "আপনার একাউন্টে লগইন করুন",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "ইমেইল বা ইউজার নেম যেকোনো একটি দিয়ে লগইন করুন",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = loginIdentifier,
                            onValueChange = { loginIdentifier = it },
                            label = { Text("ইমেইল অথবা ইউজার নেম") },
                            placeholder = { Text("Email or Username") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = "User/Email")
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_identifier_input"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = { loginPassword = it },
                            label = { Text("পাসওয়ার্ড") },
                            placeholder = { Text("Password") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = "Password")
                            },
                            trailingIcon = {
                                IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                    Icon(
                                        imageVector = if (loginPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle Password"
                                    )
                                }
                            },
                            visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { keyboardController?.hide() }
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Forgot password link
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "পাসওয়ার্ড ভুলে গেছেন? (Forgot Password)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier
                                    .clickable { showForgotPasswordDialog = true }
                                    .testTag("forgot_password_btn")
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                keyboardController?.hide()
                                errorMessage = null
                                isLoading = true
                                coroutineScope.launch {
                                    val result = viewModel.login(loginIdentifier, loginPassword)
                                    isLoading = false
                                    when (result) {
                                        is AuthResult.Success -> {
                                            Toast.makeText(context, "লগইন সফল হয়েছে!", Toast.LENGTH_SHORT).show()
                                        }
                                        is AuthResult.Error -> {
                                            errorMessage = result.message
                                        }
                                    }
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_submit_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(
                                    text = "লগইন করুন (Login)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Admin Fill Button
                        OutlinedButton(
                            onClick = {
                                loginIdentifier = "mdsiammolla0014@gmail.com"
                                loginPassword = "MDSIAMMOLLA1234@@@@"
                                Toast.makeText(context, "এডমিন তথ্য পূরণ করা হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("quick_admin_fill_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "এডমিন লগইন অটো-ফিল (Admin Credentials)",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }

                    } else {
                        // --- SIGNUP FORM ---
                        Text(
                            text = "নতুন একাউন্ট খুলুন",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "সাইনআপ করলেই পাবেন আকর্ষণীয় ৫ টাকা ওয়েলকাম বোনাস স্পিন!",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = signupName,
                            onValueChange = { signupName = it },
                            label = { Text("পূর্ণ নাম (Full Name) *") },
                            placeholder = { Text("e.g. Siam Molla") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = "Name") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_name_input"),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = signupUsername,
                            onValueChange = { signupUsername = it },
                            label = { Text("ইউজার নেম (Username) *") },
                            placeholder = { Text("e.g. siam01 (ছোট হাতের)") },
                            leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = "Username") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_username_input"),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = signupEmail,
                            onValueChange = { signupEmail = it },
                            label = { Text("ইমেইল (Email Address) *") },
                            placeholder = { Text("example@gmail.com") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_email_input"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = signupPhone,
                            onValueChange = { signupPhone = it },
                            label = { Text("মোবাইল নাম্বার (Mobile Number)") },
                            placeholder = { Text("017XXXXXXXX") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_phone_input"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Referral Code (Optional)
                        OutlinedTextField(
                            value = signupReferralCode,
                            onValueChange = { signupReferralCode = it },
                            label = { Text("রেফারেল কোড (Referral Code - Optional)") },
                            placeholder = { Text("বন্ধুর ইউজারনেম কোড") },
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = "Referral") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_referral_input"),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = signupPassword,
                            onValueChange = { signupPassword = it },
                            label = { Text("পাসওয়ার্ড (Password) *") },
                            placeholder = { Text("কমপক্ষে ৪ অক্ষরের পাসওয়ার্ড") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                            trailingIcon = {
                                IconButton(onClick = { signupPasswordVisible = !signupPasswordVisible }) {
                                    Icon(
                                        imageVector = if (signupPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle Password"
                                    )
                                }
                            },
                            visualTransformation = if (signupPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_password_input"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                keyboardController?.hide()
                                errorMessage = null
                                isLoading = true
                                coroutineScope.launch {
                                    val result = viewModel.register(
                                        name = signupName,
                                        username = signupUsername,
                                        email = signupEmail,
                                        pass = signupPassword,
                                        phone = signupPhone,
                                        referralCode = signupReferralCode
                                    )
                                    isLoading = false
                                    when (result) {
                                        is AuthResult.Success -> {
                                            Toast.makeText(context, "রেজিস্ট্রেশন সফল হয়েছে! এখন ওয়েলকাম বোনাস স্পিন ঘুরিয়ে ৫ টাকা নিন।", Toast.LENGTH_LONG).show()
                                        }
                                        is AuthResult.Error -> {
                                            errorMessage = result.message
                                        }
                                    }
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("signup_submit_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(
                                    text = "একাউন্ট তৈরি করুন (Sign Up)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // --- FORGOT PASSWORD DIALOG ---
    if (showForgotPasswordDialog) {
        var recoverIdentifier by remember { mutableStateOf("") }
        var recoverNewPass by remember { mutableStateOf("") }
        var recoverError by remember { mutableStateOf<String?>(null) }
        var recoverSuccess by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = {
                Text(
                    text = "পাসওয়ার্ড রিসেট (Forgot Password)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "আপনার একাউন্টের ইমেইল অথবা ইউজার নেম লিখুন এবং নতুন পাসওয়ার্ড সেট করুন।",
                        style = MaterialTheme.typography.bodySmall
                    )

                    OutlinedTextField(
                        value = recoverIdentifier,
                        onValueChange = { recoverIdentifier = it },
                        label = { Text("ইমেইল বা ইউজার নেম") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = recoverNewPass,
                        onValueChange = { recoverNewPass = it },
                        label = { Text("নতুন পাসওয়ার্ড (New Password)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (recoverError != null) {
                        Text(
                            text = recoverError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (recoverSuccess) {
                        Text(
                            text = "✓ পাসওয়ার্ড সফলভাবে পরিবর্তন করা হয়েছে! এখন লগইন করুন।",
                            color = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (recoverIdentifier.isBlank() || recoverNewPass.isBlank()) {
                            recoverError = "সবগুলো ফিল্ড পূরণ করুন"
                            return@Button
                        }
                        coroutineScope.launch {
                            val ok = viewModel.forgotPassword(recoverIdentifier, recoverNewPass)
                            if (ok) {
                                recoverSuccess = true
                                recoverError = null
                                Toast.makeText(context, "পাসওয়ার্ড পরিবর্তিত হয়েছে!", Toast.LENGTH_SHORT).show()
                            } else {
                                recoverError = "এই তথ্য দিয়ে কোনো একাউন্ট পাওয়া যায়নি"
                            }
                        }
                    }
                ) {
                    Text("রিসেট করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("বন্ধ করুন")
                }
            }
        )
    }
}

@Composable
private fun TabButton(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
        shadowElevation = if (isSelected) 3.dp else 0.dp,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.5.sp
                )
            )
        }
    }
}
