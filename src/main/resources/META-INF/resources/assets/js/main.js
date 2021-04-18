const BACKEND_URL = window.location.href;

$(document).ready(function() {
    $("#upload-form").submit(function(e) {
        e.preventDefault();

        let settings = {
            "url": BACKEND_URL + "upload-image",
            "type": $(this).attr("method"),
            "contentType": "multipart/form-data",
            "processData": false,
            "contentType": false,
            "cache": false,
            "data": new FormData(this)
        };
        
        let query = $.ajax(settings);
        query.fail(function (response, status) {
            console.error(response);
            
            let text = $("#upload-result-message");
            text.text("Failed to upload image!");
            text.css("color", "red");
            $("#upload-result").show();
            $("#upload-form").trigger("reset");
        });

        query.done(function (response) {
            response = JSON.parse(response);
            console.log(response);
            
            let text = $("#upload-result-message");
            if (response["status"] === "error") {
                text.text("Upload failed: " + response["message"]);
                text.css("color", "red");
            } else {
                let url = BACKEND_URL + "image/" + response["result"];
                text.html('<p>Image uploaded <a href="'+url+'">here</a>!</p>');
                text.css("color", "green");
            }
            
            $("#upload-result").show();
            $("#upload-form").trigger("reset");
        });
    });
});