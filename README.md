# FavoRecipe

Recipe and food-app for Android. Developed 2021 as part of the course ["Programmering för mobiler"](https://www.su.se/sok-kurser-och-program/ib916n-1.413325) at Stockholm university.

Built using MVVM architecture, LiveData and Jetpacks navigation components. Uses an mysql database and utilizes the Room library for database operations. Photos are loaded using Glide.

Allows user to search receipes based on dishname and/or ingredients. Recipe data is retrieved from the [Edamam recipe database](https://developer.edamam.com/edamam-recipe-api)

User may add recipes to favourites, create shoppinglists and perform CRUD operations on these. Rrecipes or shoppinglists may be shared with the app of the users choosing (using the app-chooser in the phone).

Contains functionality for geting randomized popular food videos from youtube (10 at a time, down-swipe generates 10 new videos). Videos can be viewed directly in the app or in the phone's you-tube-app or standard browser.

Videos are retrieved via the [youtube search api](https://developers.google.com/youtube/v3/docs/search/list). Videos are played using the youtubeplayer library created by [Pierfrancesco Soffritti](https://github.com/PierfrancescoSoffritti/android-youtube-player).

Launcher icon created by [Weblays](https://about.webalys.com).

App language is English and target SDK version is 31.






