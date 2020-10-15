package ru.koldaev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;
import ru.koldaev.entity.Recipe;
import ru.koldaev.entity.Review;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FindService {

    private Connection connection;
    private RecipeService recipeService;

    @Autowired
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    @Autowired
    public void setRecipeService(RecipeService recipeService)
    {
        this.recipeService = recipeService;
    }

    public List<String> getKitchenList() throws SQLException {
        List<String> kitchens = new ArrayList<>();
        String findKitchenScript = "select name from kitchen";
        PreparedStatement ps = connection.prepareStatement(findKitchenScript);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            kitchens.add(rs.getString(1));
        }
        return kitchens;
    }

    public List<String> getBaseList() throws SQLException {
        List<String> base = new ArrayList<>();
        String findBaseScript = "select name from base";
        PreparedStatement ps = connection.prepareStatement(findBaseScript);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            base.add(rs.getString(1));
        }
        return base;
    }

    public List<String> getCategoryList() throws SQLException {
        List<String> category = new ArrayList<>();
        String findCategoryScript = "select name from category";
        PreparedStatement ps = connection.prepareStatement(findCategoryScript);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            category.add(rs.getString(1));
        }
        return category;
    }

    public List<String> getCookMethodList() throws SQLException {
        List<String> cookMethod = new ArrayList<>();
        String findCookMethodScript = "select name from cooking_method";
        PreparedStatement ps = connection.prepareStatement(findCookMethodScript);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            cookMethod.add(rs.getString(1));
        }
        return cookMethod;
    }

    public List<String> getUnitsList() throws SQLException {
        List<String> unitsList = new ArrayList<>();
        String findUnitsScript = "select name from unit";
        PreparedStatement ps = connection.prepareStatement(findUnitsScript);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            unitsList.add(rs.getString(1));
        }
        return unitsList;
    }

    //Получаем список всех рецептов
    public List<Recipe> getListAllRecipe() throws SQLException {
        List<Recipe> all = new ArrayList<>();

        String getRow = "select recipe.id, recipe.name, u.login, date_added, b.name, k.name, c.name \n" +
                "    from recipe\n" +
                "    left join usr u on recipe.author_id = u.id\n" +
                "    left join base b on recipe.base_id = b.id\n" +
                "    left join kitchen k on recipe.kitchen_id = k.id\n" +
                "    left join category c on recipe.category_id = c.id;";
        String getCookMethod = "select cm.name\n" +
                "    from method_cooking_recipe mcr\n" +
                "    join cooking_method cm on mcr.cooking_method_id = cm.id\n" +
                "    where mcr.recipe_id = ?;";

        PreparedStatement ps = connection.prepareStatement(getRow);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Recipe recipe = new Recipe();

            int recipe_id = rs.getInt("id");
            recipe.setId(recipe_id);
            recipe.setName(rs.getString("name"));
            recipe.setAuthorName(rs.getString("login"));
            recipe.setDate_added(rs.getString("date_added"));
            recipe.setBase(rs.getString(5));
            recipe.setKitchen(rs.getString(6));
            recipe.setCategory(rs.getString(7));

            PreparedStatement ps1 = connection.prepareStatement(getCookMethod);
            ps1.setInt(1, recipe_id);
            ResultSet cookMethod = ps1.executeQuery();
            List<String> cookMethodList = new ArrayList<>();

            while (cookMethod.next()) {
                cookMethodList.add(cookMethod.getString(1));
            }
            recipe.setCookMethod(cookMethodList);
            all.add(recipe);
        }

        return all;
    }

    //Выбираем рецепты, которые соответствуют выбранным фильтрам
    public List<Recipe> getListFoundRecipe
            (
                    String recipeName,
                    String theBase,
                    String theKitchen,
                    String theCategory,
                    String[] theCookMethod
            ) throws SQLException {

        List<Recipe> foundRecipe = new ArrayList<>();
        List<Recipe> recipes = new ArrayList<>();

        String getRow = "select recipe.id, recipe.name, u.login, date_added, b.name, k.name, c.name \n" +
                "    from recipe\n" +
                "    left join usr u on recipe.author_id = u.id\n" +
                "    left join base b on recipe.base_id = b.id\n" +
                "    left join kitchen k on recipe.kitchen_id = k.id\n" +
                "    left join category c on recipe.category_id = c.id" +
                "    where recipe.name like ?;";
        String getCookMethod = "select cm.name\n" +
                "    from method_cooking_recipe mcr\n" +
                "    join cooking_method cm on mcr.cooking_method_id = cm.id\n" +
                "    where mcr.recipe_id = ?;";
        PreparedStatement ps = connection.prepareStatement(getRow);

        if (!StringUtils.isEmpty(recipeName)) {
            ps.setString(1, "%" + recipeName + "%");
            System.out.println("%" + recipeName + "%");
        } else {
            ps.setString(1, "%");
            System.out.println("%");
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Recipe recipe = new Recipe();

            int recipe_id = rs.getInt("id");
            recipe.setId(recipe_id);
            recipe.setName(rs.getString("name"));
            recipe.setAuthorName(rs.getString("login"));
            recipe.setDate_added(rs.getString("date_added"));
            recipe.setBase(rs.getString(5));
            recipe.setKitchen(rs.getString(6));
            recipe.setCategory(rs.getString(7));

            PreparedStatement ps1 = connection.prepareStatement(getCookMethod);
            ps1.setInt(1, recipe_id);
            ResultSet cookMethod = ps1.executeQuery();
            List<String> cookMethodList = new ArrayList<>();
            while (cookMethod.next()) {
                cookMethodList.add(cookMethod.getString(1));
            }
            recipe.setCookMethod(cookMethodList);
            recipes.add(recipe);
        }

        if (!theBase.equals("Не выбрано")) {
            recipes = recipes.stream().filter(recipe -> recipe.getBase().equals(theBase)).collect(Collectors.toList());
        }
        if (!theKitchen.equals("Не выбрано")) {
            recipes = recipes.stream().filter(recipe -> recipe.getKitchen().equals(theKitchen)).collect(Collectors.toList());
        }
        if (!theCategory.equals("Не выбрано")) {
            recipes = recipes.stream().filter(recipe -> recipe.getCategory().equals(theCategory)).collect(Collectors.toList());
        }
        if (theCookMethod.length > 0 && !theCookMethod[0].equals("Не выбрано")) {
            for (int i = 0; i < recipes.size(); i++) {
                Recipe recipe = recipes.get(i);
                boolean flag = true;
                Set<String> theRecipeCookMethods = new HashSet<>(recipe.getCookMethod());
                for (String s : theCookMethod) {
                    if (!theRecipeCookMethods.contains(s)) {
                        flag = false;
                        break;
                    }
                }
                if (flag)
                    foundRecipe.add(recipe);
            }
        } else {
            foundRecipe = recipes;
        }
        return foundRecipe;
    }

    //Получаем всю информацию о рецепте для его полного просмотра
    public Recipe getTheRecipe(int recipeId) throws SQLException
    {
        Recipe recipe = new Recipe();

        String getRecipe = "select\n" +
                "    r.name as \"name\",\n" +
                "    r.description as \"description\",\n" +
                "    u.login as \"authorName\",\n" +
                "    k.name as \"kitchenName\",\n" +
                "    c.name as \"categoryName\",\n" +
                "    b.name as \"baseName\",\n" +
                "    s.name as \"sourceName\",\n" +
                "    r.date_added as \"dateAdded\",\n" +
                "    r.portion as \"portions\",\n" +
                "    r.calorie_content as \"calories\",\n" +
                "    r.protein_content as \"proteins\",\n" +
                "    r.fat_content as \"fats\",\n" +
                "    r.carbohydrate_content as \"carbohydrates\"\n" +
                "from\n" +
                "    recipe r\n" +
                "    left join usr u on r.author_id = u.id\n" +
                "    left join kitchen k on r.kitchen_id = k.id\n" +
                "    left join category c on r.category_id = c.id\n" +
                "    left join base b on r.base_id = b.id\n" +
                "    left join source s on r.source_id = s.id\n" +
                "where r.id = ?;";

        String recipeComposition = "select\n" +
                "    i.name as \"name\",\n" +
                "    rc.count as \"count\",\n" +
                "    u.name as \"unit\"\n" +
                "from\n" +
                "    recipe_composition rc\n" +
                "    join ingredient i on rc.ingredient_id = i.id\n" +
                "    join unit u on rc.unit_id = u.id\n" +
                "where rc.recipe_id = ?;";

        String recipeCookMethod = "select\n" +
                "    cm.name as \"cookMethod\"\n" +
                "from\n" +
                "    method_cooking_recipe mcr\n" +
                "    join cooking_method cm on mcr.cooking_method_id = cm.id\n" +
                "where\n" +
                "    mcr.recipe_id = ?;";

        PreparedStatement ps = connection.prepareStatement(getRecipe);
        ps.setInt(1, recipeId);
        ResultSet rs = ps.executeQuery();

        PreparedStatement ps1 = connection.prepareStatement(recipeComposition);
        ps1.setInt(1, recipeId);
        ResultSet rs1 = ps1.executeQuery();

        PreparedStatement ps2 = connection.prepareStatement(recipeCookMethod);
        ps2.setInt(1, recipeId);
        ResultSet rs2 = ps2.executeQuery();

        while (rs.next())
        {
            recipe.setId(recipeId);
            recipe.setName(rs.getString("name"));
            recipe.setDescription(rs.getString("description"));
            recipe.setAuthorName(rs.getString("authorName"));

            String kitchenName = rs.getString("kitchenName");
            recipe.setKitchen(StringUtils.isEmpty(kitchenName) ? "Не выбрано" : kitchenName);

            String categoryName = rs.getString("categoryName");
            recipe.setCategory(StringUtils.isEmpty(categoryName) ? "Не выбрано" : categoryName);

            String baseName = rs.getString("baseName");
            recipe.setBase(StringUtils.isEmpty(baseName) ? "Не выбрано" : baseName);

            recipe.setSource(rs.getString("sourceName"));
            recipe.setDate_added(rs.getString("dateAdded"));

            String portions_string = rs.getString("portions");
            int portions = StringUtils.isEmpty(portions_string) ? 0 : Integer.parseInt(portions_string);
            recipe.setPortion(portions);

            String calories_string = rs.getString("calories");
            int calories = StringUtils.isEmpty(calories_string) ? 0 : Integer.parseInt(calories_string);
            recipe.setCalories(calories);

            String proteins_string = rs.getString("proteins");
            int proteins = StringUtils.isEmpty(proteins_string) ? 0 : Integer.parseInt(proteins_string);
            recipe.setProteins(proteins);

            String fats_string = rs.getString("fats");
            int fats = StringUtils.isEmpty(fats_string) ? 0 : Integer.parseInt(fats_string);
            recipe.setFats(fats);

            String carbohydrates_string = rs.getString("carbohydrates");
            int carbohydrates = StringUtils.isEmpty(carbohydrates_string) ? 0 : Integer.parseInt(carbohydrates_string);
            recipe.setCarbohydrate(carbohydrates);
        }

        List<String> ingredients = new ArrayList<>();
        List<String> countIngredients = new ArrayList<>();
        List<String> units = new ArrayList<>();
        List<String> oneCompositionRow = new ArrayList<>();
        while (rs1.next())
        {
//            ingredients.add(rs1.getString("name"));
//            countIngredients.add(rs1.getString("count"));
//            units.add(rs1.getString("unit"));
            oneCompositionRow.add(rs1.getString("name") + " " + rs1.getString("count") + " " + rs1.getString("unit"));
        }
//        recipe.setIngredient(ingredients);
//        recipe.setCountIngredient(countIngredients);
//        recipe.setUnit(units);
        recipe.setIngredient(oneCompositionRow);

        List<String> cookMethods = new ArrayList<>();
        while (rs2.next())
        {
            cookMethods.add(rs2.getString("cookMethod"));
        }
        recipe.setCookMethod(cookMethods);

        return recipe;
    }

    //Список отзывов конктретного рецепта
    public List<Review> getReviewList(int recipeId) throws SQLException
    {
        List<Review> reviewList = new ArrayList<>();
        String getReviewQuery = "select\n" +
                "    u.login as \"user_login\",\n" +
                "    r.content as \"review\",\n" +
                "    r.date_added as \"date\"\n" +
                "from\n" +
                "    review r\n" +
                "    join usr u on r.user_id = u.id\n" +
                "    where r.recipe_id = ?" +
                "    order by r.id desc;";
        PreparedStatement ps = connection.prepareStatement(getReviewQuery);
        ps.setInt(1, recipeId);
        ResultSet rs = ps.executeQuery();

        while (rs.next())
        {
            Review review = new Review();
            review.setRecipeId(recipeId);
            review.setUser(rs.getString("user_login"));
            review.setReview(rs.getString("review"));
            review.setDate(rs.getString("date"));
            reviewList.add(review);
        }

        return reviewList;
    }

    //Список отзывов на все рецепты контретного автора
    public List<Review> getReviewListByUser(String userLogin) throws SQLException
    {
        List<Review> reviewList = new ArrayList<>();
        int authorId = recipeService.getAuthorId(userLogin);

        String query = "select\n" +
                "    r2.name as \"recipe_name\",\n" +
                "    u.login as \"user_login\",\n" +
                "    r.content as \"review\",\n" +
                "    r.date_added as \"date\"\n" +
                "from\n" +
                "    review r\n" +
                "    join usr u on r.user_id = u.id\n" +
                "    join recipe r2 on r.recipe_id = r2.id\n" +
                "    where r2.author_id = ?" +
                "    order by r.id desc;";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, authorId);
        ResultSet rs = ps.executeQuery();

        while (rs.next())
        {
            Review review = new Review();
            review.setRecipeName(rs.getString("recipe_name"));
            review.setUser(rs.getString("user_login"));
            review.setReview(rs.getString("review"));
            review.setDate(rs.getString("date"));
            reviewList.add(review);
        }

        return reviewList;
    }

    public List<Recipe> getListFavoriteRecipe(String userLogin) throws SQLException
    {
        List<Recipe> favorites = new ArrayList<>();

        int userId = recipeService.getAuthorId(userLogin);

        String getRow = "select recipe.id, recipe.name, u.login, date_added, b.name, k.name, c.name\n" +
                "    from recipe\n" +
                "    left join usr u on recipe.author_id = u.id\n" +
                "    left join base b on recipe.base_id = b.id\n" +
                "    left join kitchen k on recipe.kitchen_id = k.id\n" +
                "    left join category c on recipe.category_id = c.id\n" +
                "    join favorites f on recipe.id = f.recipe_id\n" +
                "    where f.user_id = ?;";
        String getCookMethod = "select cm.name\n" +
                "    from method_cooking_recipe mcr\n" +
                "    join cooking_method cm on mcr.cooking_method_id = cm.id\n" +
                "    where mcr.recipe_id = ?;";

        PreparedStatement ps = connection.prepareStatement(getRow);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Recipe recipe = new Recipe();

            int recipe_id = rs.getInt("id");
            recipe.setId(recipe_id);
            recipe.setName(rs.getString("name"));
            recipe.setAuthorName(rs.getString("login"));
            recipe.setDate_added(rs.getString("date_added"));
            recipe.setBase(rs.getString(5));
            recipe.setKitchen(rs.getString(6));
            recipe.setCategory(rs.getString(7));

            PreparedStatement ps1 = connection.prepareStatement(getCookMethod);
            ps1.setInt(1, recipe_id);
            ResultSet cookMethod = ps1.executeQuery();
            List<String> cookMethodList = new ArrayList<>();

            while (cookMethod.next()) {
                cookMethodList.add(cookMethod.getString(1));
            }
            recipe.setCookMethod(cookMethodList);
            favorites.add(recipe);
        }

        return favorites;
    }
}
