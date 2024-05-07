package com.developerspace.webrtcsample.compose.ui.util

import com.developerspace.webrtcsample.R

data class Topic(
    var drawableID: Int = 0,
    var topicTitle: String = "",
    var quoteText: String = "",
    var extraQuoteText: String = ""
)

val staticTopicList = arrayListOf(
    Topic(
        R.drawable.childhood,
        "Childhood",
        quoteText = "One of the luckiest things that can happen to you in life is, I think, to have a happy childhood!\n\n~Agatha Cristie",
        extraQuoteText = "Choose a job you love, and you will never have to work a day in your life.\n\n~Confucius"
    ),
    Topic(
        R.drawable.hobby,
        "Hobby",
        quoteText = "Find three hobbies you love: one to make you money, one to keep you in shape, and one to be creative.\n\n~Anonymous",
        extraQuoteText = "Hobbies are for pleasure, not pressure.\n\n~Adabella Radici"
    ),
    Topic(
        R.drawable.music,
        "Music",
        quoteText = "Music can change the world because it can change people.\n\n~Bono",
        extraQuoteText = "Where words fail, music speaks.\n\n~Hans Christian Andersen"
    ),
    Topic(
        R.drawable.food,
        "Food",
        quoteText = "One cannot think well, love well, sleep well, if one has not dined well.\n\n~Virginia Woolf",
        extraQuoteText = "Tell me what you eat, and I will tell you what you are.\n\n~Jean Anthelme Brillat-Savarin"
    ),
    Topic(
        R.drawable.politics,
        "Politics",
        quoteText = "In politics, nothing happens by accident. If it happens, you can bet it was planned that way.\n\n~Franklin D. Roosevelt",
        extraQuoteText = "The most powerful weapon in politics is the ability to ignore the facts.\n\n~William J. Clinton"
    ),
    Topic(
        R.drawable.sports,
        "Sports",
        quoteText = "The way a team plays as a whole determines its success. You may have the greatest bunch of individual stars in the world, but if they don't play together, the club won't be worth a dime.\n\n~Babe Ruth",
        extraQuoteText = "One of the luckiest things that can happen to you in life is, I think, to have a happy childhood!\n\n~Agatha Cristie"
    ),
    Topic(
        R.drawable.penpal,
        "Pen Pal",
        quoteText = "A penpal is a treasure, a lifelong friend waiting to be discovered across the miles.\n\n~Susan Branch",
        extraQuoteText = "In a world of fleeting connections, a penpal offers the timeless beauty of written words and lasting friendship.\n\n~Laura Ingalls Wilder",
    ),
    Topic(
        R.drawable.relationships,
        "Relationship",
        quoteText = "The meeting of two personalities is like the contact of two chemical substances: if there is any reaction, both are transformed\n\n~Carl Jung",
        extraQuoteText = "The quality of your life is the quality of your relationships.\n\n~Anthony Robbins",
    ),
    Topic(
        R.drawable.loneliness,
        "Loneliness",
        quoteText = "The greatest thing in the world is to know how to belong to oneself.\n\n~Michel de Montaigne",
        extraQuoteText = "The greatest loneliness is to not be comfortable with yourself.\n\n~Mark Twain"
    ),
)