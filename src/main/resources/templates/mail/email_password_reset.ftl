<html lang="en">
<head>
    <style rel="text/css">
        p {
        }

        p.title {
            font-weight: bold;
        }

        p.headline span, .text-bold {
            font-weight: bold;
        }

        /* -------------------------------------
               FOOTER
       ------------------------------------- */
        table.footer-wrap {
            width: 100%;
            clear: both !important;
        }

        .footer-wrap .container td.content p {
            border-top: 1px solid rgb(215, 215, 215);
            padding-top: 15px;
        }

        .footer-wrap .container td.content p {
            font-size: 10px;
            font-weight: bold;
        }
    </style>
    <title>Password Reset</title>
</head>
<body>
<p class="title">Forgot Password</p>
<p class="headline"><span>Headline:</span> Reset Your Password </p>
<p>Hello ${EMAIL_FULL_NAME}</p>
<p>We have received your password reset request and the system has generated a link for you to change your password.
</p>
<p>Please click the link below to change your password</p>
<p><a href="${EMAIL_PASSWORD_RESET_URL}" target="_blank">${EMAIL_PASSWORD_RESET_URL}</a></p>
<p>If the request was not made by you kindly ignore this message</p>
<p>Best Regards,</p>
<!-- FOOTER -->
<table class="footer-wrap">
    <tr>
        <td></td>
        <td class="container">
            <!-- content -->
            <div class="content">
                <p>
                    <table>
                        <tr>
                            <td align="center">
                <p>
                    ${EMAIL_DISCLAIMER}
                </p>
                <p>
                    ${EMAIL_SPAM_DISCLAIMER}
                </p>
                <p>
                    ${EMAIL_FOOTER_COPYRIGHT}
                </p>
        </td>
    </tr>
</table>
</p>
</div><!-- /content -->

</td>
<td></td>
</tr>
</table><!-- /FOOTER -->

</body>
</html>