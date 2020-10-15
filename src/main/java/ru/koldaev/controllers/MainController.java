package ru.koldaev.controllers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;
import ru.koldaev.entity.Recipe;
import ru.koldaev.entity.Review;
import ru.koldaev.entity.User;
import ru.koldaev.service.FindService;
import ru.koldaev.service.RecipeService;
import ru.koldaev.service.UserService;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Controller
@SessionAttributes(value = "currentUser")
public class MainController {

    private FindService findService;
    private UserService userService;
    private RecipeService recipeService;

    @Autowired
    public void setFindService(FindService findService) {
        this.findService = findService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRecipeService(RecipeService recipeService) {
        this.recipeService = recipeService;
    }


    private Model getBaseValues(Model model) throws SQLException {
        List<String> kitchens = findService.getKitchenList();
        List<String> bases = findService.getBaseList();
        List<String> categories = findService.getCategoryList();
        List<String> cookingMethod = findService.getCookMethodList();
        model.addAttribute("kitchensList", kitchens);
        model.addAttribute("baseList", bases);
        model.addAttribute("categoriesList", categories);
        model.addAttribute("cookMethodList", cookingMethod);
        return model;
    }

    @GetMapping("/main")
    public String getFirstPage(Model model, HttpSession session) throws SQLException {

        model = getBaseValues(model);

        if (session.getAttribute("currentUser") == null) {
            return "errorPage";
        }
        List<Recipe> allRecipeList = findService.getListAllRecipe();
        model.addAttribute("allRecipeList", allRecipeList);
        return "index";
    }

    @PostMapping("/main")
    public String findRecipe
            (
                    Model model,
                    HttpSession session,
                    @RequestParam(required = false) String recipeName,
                    @RequestParam(required = false) String theBase,
                    @RequestParam(required = false) String theKitchen,
                    @RequestParam(required = false) String theCategory,
                    @RequestParam(required = false) String[] theCookMethod
            ) throws SQLException {
        List<Recipe> foundRecipeList = findService.getListFoundRecipe(recipeName, theBase, theKitchen, theCategory, theCookMethod);
        model = getBaseValues(model);
        model.addAttribute("allRecipeList", foundRecipeList);
        return "index";
    }

    @GetMapping("/createRecipe")
    public String getCreateRecipePage(Model model) throws SQLException {
        model = getBaseValues(model);
        model.addAttribute("unitsList", findService.getUnitsList());
        return "createRecipe";
    }

    @PostMapping("/createRecipe")
    public String createRecipe
            (
                    @RequestParam String recipeName,
                    @RequestParam String theBase,
                    @RequestParam String theKitchen,
                    @RequestParam String theCategory,
                    @RequestParam String[] theCookMethod,
                    @RequestParam String[] ingredientName,
                    @RequestParam String[] countName,
                    @RequestParam String[] unitName,
                    @RequestParam String description,
                    @RequestParam(required = false) String calories,
                    @RequestParam(required = false) String proteins,
                    @RequestParam(required = false) String fats,
                    @RequestParam(required = false) String carbohydrates,
                    @RequestParam(required = false) String portions,
                    @RequestParam String source,
                    HttpSession session
            ) throws SQLException {
//        System.out.println(recipeName);
//        System.out.println(theBase);
//        System.out.println(theKitchen);
//        System.out.println(theCategory);
//        for (String s : theCookMethod) {
//            System.out.println(s);
//        }
//        for (int i = 0; i < ingredientName.length; i++) {
//            System.out.print(ingredientName[i] + "   ");
//            System.out.print(countName[i] + "   ");
//            System.out.print(unitName[i]);
//            System.out.println();
//        }
//        System.out.println(description);
//        System.out.println(calories);
//        System.out.println(proteins);
//        System.out.println(fats);
//        System.out.println(carbohydrates);
//        System.out.println(portions);
//        System.out.println(source);

        String calories1 = "";
        String proteins1 = "";
        String fats1 = "";
        String carbohydrates1 = "";
        String portions1 = "";
        if (!StringUtils.isEmpty(calories)) {
            calories1 = calories;
        }
        if (!StringUtils.isEmpty(proteins)) {
            proteins1 = proteins;
        }
        if (!StringUtils.isEmpty(fats)) {
            fats1 = fats;
        }
        if (!StringUtils.isEmpty(carbohydrates)) {
            carbohydrates1 = carbohydrates;
        }
        if (!StringUtils.isEmpty(portions)) {
            portions1 = portions;
        }

        recipeService.createRecipe
                (
                        recipeName,
                        theBase,
                        theKitchen,
                        theCategory,
                        theCookMethod,
                        ingredientName,
                        countName,
                        unitName,
                        description,
                        calories1,
                        proteins1,
                        fats1,
                        carbohydrates1,
                        portions1,
                        source,
                        ((User) session.getAttribute("currentUser")).getLogin()
                );
        return "index";
    }

    @GetMapping("/seeTheRecipe")
    public String seeTheRecipe(Model model, HttpSession session, @RequestParam int recipeId) throws SQLException
    {
        Recipe recipe = findService.getTheRecipe(recipeId);
        List<Review> reviewList = findService.getReviewList(recipeId);
        model.addAttribute("recipe", recipe);
        model.addAttribute("reviewList", reviewList);
        return "seeTheRecipe";
    }

    @PostMapping("/addReview")
    public String addReview(HttpSession session, @RequestParam int recipeIdForReview, @RequestParam String review) throws SQLException
    {
        String user_login = ((User) session.getAttribute("currentUser")).getLogin();
        recipeService.addReview(user_login, recipeIdForReview, review);
        return "redirect:/seeTheRecipe?recipeId=" + recipeIdForReview;
    }

    @PostMapping("/addToFavorite")
    @ResponseBody
    public Map<String, String> addToFavorite(HttpSession session, @RequestBody Map<String, String> map) throws SQLException
    {

        int recipeId = Integer.parseInt(map.get("recipeId"));
        String userLogin = ((User) session.getAttribute("currentUser")).getLogin();

        String isFavorite = map.get("isFavorite");

        if (isFavorite.equals("Добавить в избранное"))
        {
            map.put("isFavorite", "Удалить из избранного");
            recipeService.addToFavorite(userLogin, recipeId);
        }
        else
        {
            map.put("isFavorite", "Добавить в избранное");
            recipeService.deleteFromFavorite(userLogin, recipeId);
        }

        return map;
    }

    @GetMapping("/seeReviewOnMyRecipe")
    public String getReviewOnMyRecipe(HttpSession session, Model model) throws SQLException
    {
        String userLogin = ((User) session.getAttribute("currentUser")).getLogin();
        List<Review> reviewList = findService.getReviewListByUser(userLogin);
        model.addAttribute("reviewList", reviewList);
        return "notifications";
    }

    @GetMapping("/favorites")
    public String getFavoriteRecipePage(HttpSession session, Model model) throws SQLException
    {
        String userLogin = ((User) session.getAttribute("currentUser")).getLogin();
        List<Recipe> favorites = findService.getListFavoriteRecipe(userLogin);
        model.addAttribute("favoriteRecipeList", favorites);
        return "favoriteRecipe";
    }

}
