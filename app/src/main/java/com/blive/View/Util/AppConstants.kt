package com.blive.View.Util

import com.blive.BuildConfig


class AppConstants {
    companion object{
        val WebViewURL:String="https://www.google.co.in/"

        //Base URL
        var domain: String = BuildConfig.api_host_name
        var webDomain: String = BuildConfig.web_host_name
        var agoraAAPI = "https://api.agora.io/dev/v1"

        var updateImage = domain + "updateUserProfile"
        var uploadVideo = domain + "video"

        //Web URL's
        var aboutUs = webDomain + "Terms/V1/aboutus.php"
        var suggestions = webDomain + "Terms/V1/suggestions.php?user_id="
        var level = webDomain + "Terms/V1/level.php?userID="
        var agreement = webDomain + "Terms/V1/streameragreement.php"
        var termsAndConditions = webDomain + "Terms/V1/privacypolicy.php"
        var wallet = webDomain + "purchase/wallet07.php?user_id="
        var rewards = webDomain + "rewards/Rewards.php?user_id="
        var dailyTask = webDomain + "dailyTask/dailyTask.php?user_id="
        var progress = webDomain + "myProgress/myProgress.php?user_id="
        var assets = webDomain + "myAssets/myasset18.php?user_id="
        var helpAndFAQ = webDomain + "Terms/V1/helpfeedback.php"
        var topperBanner = webDomain + "banners/worldtopperbanner.php"
        var DailyCheckin = webDomain + "checkin/checkin.php?user_id="
        var Daily_spin = webDomain + "Games/spin/free_spin.php?user_id="
        var freeGiftWeb = webDomain + "freegift/modal1.php"
        var treasureBox = webDomain + "treasure/treasure.php"
        var addAudience = "addAudience"
        var getAudience = "audience"
        var getGuest = "guest"
        var defaultlength = "10"
        var defaultpage = "1"
        val addguest: String="addGuest"
        val removeGuest: String="removeGuest"
        val getGift: String="giftList"


        var topper_1 =
            "https://s3.ap-south-1.amazonaws.com/blive/others/topper1.webp"
        var topper_2 =
            "https://s3.ap-south-1.amazonaws.com/blive/others/topper2.webp"
        var topper_3 =
            "https://s3.ap-south-1.amazonaws.com/blive/others/topper3.webp"

        // Star Value
        var starValue =
            "https://s3.ap-south-1.amazonaws.com/blive/WebpageAsserts/StarLvL/Star"

        //Banner
        var index1 = webDomain + "banners/Banner1/index.php"

        //Level Up Image
        var levelUp =
            "https://s3.ap-south-1.amazonaws.com/blive/WebpageAsserts/Level_Up01.png"

        // New Mobile User Login
        var new_User_Mobile =
            "https://blive.s3.ap-south-1.amazonaws.com/100th+gift_gif/signup_reward.webp"

        // 100th Free Gift Acheived
        var hun_Free_Gift_Acheived =
            "https://blive.s3.ap-south-1.amazonaws.com/100th+gift_gif/100th_Gift.webp"

        //News
        var news =
            "https://s3.ap-south-1.amazonaws.com/blive/OfferPage/special-offer-button/index.html"

        //!00th FreeGift WebP
        var free_Gift_Level_100th =
            "https://blive.s3.ap-south-1.amazonaws.com/100th+gift_gif/100th_Gift.webp"

        //Guest Frame
        var guest_Frame =
            "https://blive.s3.ap-south-1.amazonaws.com/frames/guetsFrame1.webp"

        // Games
        var games = webDomain + "Games/index.php?user_id="

        //treasure
        var getTreasureBox =
            "https://blive.s3.ap-south-1.amazonaws.com/gifts_V1_Beta/Treasure_Chest.webp"

        //Mid Bannner
        var midBanner = webDomain + "banners/Banner1/middle_banner.php"

        //Messages
        var messages = webDomain + "message/index.php?user_id="

        //share API
        var shareAPI = webDomain + "link/index.html?user_id="
        var commonExcepetion: String?="Service error"

        var getTopperList = webDomain + "topper/index.php?user_id="
    }
}