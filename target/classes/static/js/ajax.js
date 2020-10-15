$(document).ready(function() {
    $('#favorites').submit(function(e) {
        e.preventDefault();
        var recipeId = {}
        recipeId["recipeId"] = $("#recipeId").val();
        //Определяем в избранном рецепт или нет
        recipeId["isFavorite"] = $("#isFavorite").val();

        console.log("SUCCESS : ");
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/addToFavorite",
            data: JSON.stringify(recipeId),
            dataType: 'json',
            cache: false,
            success: function (data) {

                //$('#recipeInFavorite').html(data.Hello);
                $('#isFavorite').val(data.isFavorite);
                console.log("SUCCESS : ", data);
            },
            error: function (e) {
                $('#recipeInFavorite').html("Error");
                console.log("ERROR : ", e);
            }
        })
        ;
    });
});