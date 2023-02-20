$(document).ready(function () {
    $("#btnCancel").on("click", function () {
        window.location = moduleURL;
    });

    $("#fileImage").change(function () {
        var fileSize = this.files[0].size;
        console.log("File size: " + fileSize);
        // 1024 * 11024 = 1048576
        if (fileSize > 10485760000) {
            this.setCustomValidity("You must choose an image less  than 1 MB");
            this.reportValidity();
        }
        else {
            this.setCustomValidity("");
            showImageThumbnail(this);
        }
    });
});

function showImageThumbnail(fileInput) {
    var file = fileInput.files[0];
    var reader = new FileReader();

    console.log("Show thumbnail....");
    reader.onload = function (e) {
        $("#thumbnail").attr("src", e.target.result);
    }

    reader.readAsDataURL(file);
}