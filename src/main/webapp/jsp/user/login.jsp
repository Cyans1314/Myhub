<%--
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: User login page
 * ============================================================================
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign in to MyHub</title>

    <!-- Reference external CDN icon library -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet" />


    <style>
        /* Global font */
        body {
            background-color: #f6f8fa;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            margin: 0;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji";
            color: #24292f;
        }

        /* Top Logo */
        .header-logo {
            padding-bottom: 24px;
            color: #24292f;
            text-align: center;
        }

        /* Title */
        .auth-form-header {
            text-align: center;
            margin-bottom: 15px;
        }
        .auth-form-header h1 {
            font-size: 24px;
            font-weight: 300;
            letter-spacing: -0.5px;
            margin: 0;
        }

        /* Login form container */
        .auth-form {
            width: 308px;
            margin: 0 auto;
        }

        /* Login form body */
        .auth-form-body {
            background-color: #fff;
            border: 1px solid #d8dee4;
            border-radius: 6px;
            padding: 20px;
            font-size: 14px;
            margin-top: 16px;
            box-shadow: 0 8px 24px rgba(140,149,159,0.2);
        }

        /* Label */
        label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            font-size: 14px;
        }

        /* Input field (with blue focus effect) */
        .form-control {
            width: 100%;
            padding: 5px 12px;
            font-size: 14px;
            line-height: 20px;
            color: #24292f;
            vertical-align: middle;
            background-color: #fff;
            background-repeat: no-repeat;
            background-position: right 8px center;
            border: 1px solid #d0d7de;
            border-radius: 6px;
            outline: none;
            box-sizing: border-box;
            height: 32px;
            margin-bottom: 16px;
            transition: 80ms cubic-bezier(0.33, 1, 0.68, 1);
        }

        .form-control:focus {
            border-color: #0969da;
            box-shadow: 0 0 0 3px rgba(9, 105, 218, 0.3);
        }

        /* Green button */
        .btn-green {
            display: block;
            width: 100%;
            text-align: center;
            background-color: #2da44e;
            color: #ffffff;
            border: 1px solid rgba(27,31,36,0.15);
            border-radius: 6px;
            padding: 5px 16px;
            font-size: 14px;
            font-weight: 600;
            line-height: 20px;
            cursor: pointer;
            margin-top: 20px;
        }
        .btn-green:hover {
            background-color: #2c974b;
        }

        /* Link style */
        .forgot-password {
            float: right;
            font-size: 12px;
            color: #0969da;
            text-decoration: none;
            font-weight: normal;
        }

        /* Bottom registration box */
        .create-account-box {
            border: 1px solid #d0d7de;
            border-radius: 6px;
            padding: 15px 20px;
            text-align: center;
            margin-top: 16px;
            font-size: 14px;
        }
        .create-account-box a {
            color: #0969da;
            text-decoration: none;
        }
        .create-account-box a:hover {
            text-decoration: underline;
        }

        /* Error message */
        .flash-error {
            background-color: #ffebe9;
            border: 1px solid rgba(255,129,130,0.4);
            color: #cf222e;
            padding: 15px;
            border-radius: 6px;
            font-size: 13px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
    </style>
</head>
<body>

<!-- Logo -->
<div class="header-logo">
    <i class="fab fa-github fa-3x"></i>
</div>

<!-- Title -->
<div class="auth-form-header">
    <h1>Sign in to MyHub</h1>
</div>

<!-- Form area -->
<div class="auth-form">
    <div class="auth-form-body">
        <!-- Error message -->
        <c:if test="${not empty error}">
            <div class="flash flash-error">
                    ${error}
                <i class="fas fa-times" style="cursor:pointer;" onclick="this.parentElement.style.display='none'"></i>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/user/login" method="post">
            <!-- Username -->
            <label for="username">Username or email address</label>
            <input type="text" name="username" id="username" class="form-control" autofocus required />

            <!-- Password -->
            <div style="position: relative;">
                <label for="password">Password</label>
                <a href="#" class="forgot-password">Forgot password?</a>
            </div>
            <input type="password" name="password" id="password" class="form-control" required />

            <!-- Sign in button -->
            <button type="submit" class="btn-green">Sign in</button>
        </form>
    </div>

    <!-- Registration guide -->
    <div class="create-account-box bg-white">
        New to MyHub? <a href="${pageContext.request.contextPath}/user/register">Create an account</a>.
    </div>

    <!-- Bottom placeholder -->
    <div style="height: 50px;"></div>
</div>

</body>
</html>