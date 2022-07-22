<html lang="en">
<head>
    <style rel="text/css">
        p {
        }

        p.title {
            font-weight: bold;
        }

        p.headline span,.text-bold {
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
    <title>Sample Email</title>
</head>
<body>

<p class="title">Sample Email</p>
<p class="headline"><span>Headline:</span> Congratulations </p>
<p>Dear ${EMAIL_FULL_NAME}</p>
<p>Welcome to Decapay Platform!</p>

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
                <p>${EMAIL_DISCLAIMER}</p>
                <p>${EMAIL_SPAM_DISCLAIMER}</p>
                <p>${EMAIL_FOOTER_COPYRIGHT}</p>
        </td>
    </tr>
</table>
</div><!-- /content -->

</td>
<td></td>
</tr>
</table><!-- /FOOTER -->
</body>
</html>