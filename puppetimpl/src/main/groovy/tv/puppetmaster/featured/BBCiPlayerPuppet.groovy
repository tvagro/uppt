package tv.puppetmaster.featured

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator

import javax.xml.parsers.DocumentBuilderFactory
import java.util.regex.Matcher

class BBCiPlayerPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 4

    static final CHANNELS = [
            [
                    name:               "BBC One",
                    genres:             "NEWS",
                    description:        "Broadcasting mainstream comedy, drama, documentaries, films, news, sport, and children's programmes.",
                    url:                "http://www.bbc.co.uk/bbcone/a-z",
                    imageUrl:           "http://www.throup.org.uk/images/doctor_who/bbc_one.png",
                    backgroundImageUrl: "http://www.throup.org.uk/images/doctor_who/bbc_one.png",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio_video/simulcast/hls/uk/abr_hdtv/ak/bbc_one_hd.m3u8",
            ],
            [
                    name:               "BBC Two",
                    genres:             "ENTERTAINMENT",
                    description:        "Specialist programming and minority interest programmes.",
                    url:                "http://www.bbc.co.uk/bbctwo/a-z",
                    imageUrl:           "https://s-media-cache-ak0.pinimg.com/736x/5f/3b/53/5f3b5311a3db51d778d8fc8cfad77d97.jpg",
                    backgroundImageUrl: "https://i.ytimg.com/vi/Vrq9shq0k4o/maxresdefault.jpg",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio_video/simulcast/hls/uk/abr_hdtv/ak/bbc_two_hd.m3u8",
            ],
            [
                    name:               "BBC Three",
                    genres:             "ENTERTAINMENT",
                    description:        "Home to mainly youth-oriented programming, particularly new comedy sketch shows and sitcoms.",
                    url:                "http://www.bbc.co.uk/tv/bbcthree/a-z",
                    imageUrl:           "https://d13yacurqjgara.cloudfront.net/users/484796/screenshots/2438904/bbc3-logofix_1x.jpg",
                    backgroundImageUrl: "http://www.theyorker.co.uk/wp-content/uploads/2016/02/bbc-3-three-logo.png",
            ],
            [
                    name:               "BBC Four",
                    genres:             "TECH_SCIENCE,EDUCATION",
                    description:        "Niche programming for an intellectual audience.",
                    url:                "http://www.bbc.co.uk/bbcfour/a-z",
                    imageUrl:           "http://theeurotvplace.com/wp-content/uploads/2015/08/BBC-Four-logo.jpg",
                    backgroundImageUrl: "http://www.redbeecreative.com/storage/imagecache/poster_medium/work/bbc-four.jpg",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio_video/simulcast/hls/uk/abr_hdtv/ak/bbc_four_hd.m3u8",
            ],
            [
                    name:               "CBBC",
                    genres:             "FAMILY_KIDS",
                    description:        "For children aged seven and above.",
                    url:                "http://www.bbc.co.uk/tv/cbbc/a-z",
                    imageUrl:           "http://www.astra2sat.com/wp-content/uploads/2016/03/CBBC-Logo.jpg",
                    backgroundImageUrl: "http://www.redbeecreative.com/storage/imagecache/poster_medium/work_block_carousel/cbbc-logo1-15040.jpg",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio_video/simulcast/hls/uk/abr_hdtv/ak/cbbc_hd.m3u8",
            ],
            [
                    name:               "CBeebies",
                    genres:             "FAMILY_KIDS",
                    description:        "For children under seven.",
                    url:                "http://www.bbc.co.uk/tv/cbeebies/a-z",
                    imageUrl:           "http://etcandroid.com/wp-content/uploads/2015/07/cbeebies-videos-and-games.png",
                    backgroundImageUrl: "http://www.mauloni.com/wp-content/uploads/2014/08/CBeebies.jpg",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio_video/simulcast/hls/uk/abr_hdtv/ak/cbeebies_hd.m3u8",
            ],
            [
                    name:               "BBC News Channel",
                    genres:             "NEWS",
                    description:        "A dedicated news channel.",
                    url:                "http://www.bbc.co.uk/tv/bbcnews/a-z",
                    imageUrl:           "http://www.providencetalks.org/wp-content/uploads/2014/03/bbc2-300x300.png",
                    backgroundImageUrl: "http://vignette2.wikia.nocookie.net/logopedia/images/0/00/BBC_News_Generic.png",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio_video/simulcast/hls/uk/abr_hdtv/ak/bbc_news24.m3u8",
            ],
            [
                    name:               "أخبار بي بي سي",
                    genres:             "NEWS",
                    description:        "البث المباشر لتلفزيون بي بي سي عربي.",
                    imageUrl:           "http://static.radio.net/images/broadcasts/0a/d4/27575/c300.png",
                    backgroundImageUrl: "http://vignette2.wikia.nocookie.net/logopedia/images/0/00/BBC_News_Generic.png",
                    liveStreamUrl:      "http://bbcwshdlive01-lh.akamaihd.net/i/atv_1@61433/master.m3u8",
            ],
            [
                    name:               "تلویزیون فارسی بی‌بی‌سی",
                    genres:             "NEWS",
                    description:        "برنامه های تلویزیون فارسی، هر روز به طور مستقیم و زنده از وبسایت فارسی بی‌بی‌سی نیزپخش می شود.",
                    imageUrl:           "https://lh3.googleusercontent.com/F_h0WRcNxAPGUyE_hPdKck4V9qfj6U1D4iCvsF011j9VgCWAy1_X8nCPY6qazPwFNDa9=w300",
                    backgroundImageUrl: "http://vignette2.wikia.nocookie.net/logopedia/images/0/00/BBC_News_Generic.png",
                    liveStreamUrl:      "http://bbcwshdlive01-lh.akamaihd.net/i/ptv_1@78015/master.m3u8",
            ],
            [
                    name:               "BBC Parliament",
                    genres:             "NEWS",
                    description:        "Dedicated politics channel, covering both the UK Parliament, Scottish Parliament, Welsh Assembly, Northern Ireland Assembly, and international politics.",
                    url:                "http://www.bbc.co.uk/tv/bbcparliament/a-z",
                    imageUrl:           "https://static.filmon.com/assets/channels/1666/extra_big_logo.png",
                    backgroundImageUrl: "https://upload.wikimedia.org/wikipedia/en/thumb/4/49/BBC_Parliament_Logo.svg/1280px-BBC_Parliament_Logo.svg.png",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio_video/simulcast/hls/uk/hls_tablet/ak/bbc_parliament.m3u8",
            ],
            [
                    name:               "Alba",
                    genres:             "NEWS",
                    description:        "A part-time Scottish Gaelic channel.",
                    url:                "http://www.bbc.co.uk/tv/bbcalba/a-z",
                    imageUrl:           "https://freebets.uk/img/channels/300x300/BBC-ALBA.png",
                    backgroundImageUrl: "https://upload.wikimedia.org/wikipedia/en/thumb/a/a5/BBC_Alba.svg/1280px-BBC_Alba.svg.png",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio_video/simulcast/hls/uk/hls_tablet/ak/bbc_alba.m3u8",
            ],
            [
                    name:               "S4C",
                    genres:             "NEWS",
                    description:        "A Welsh-language public-service television channel.",
                    url:                "http://www.bbc.co.uk/tv/s4c/a-z",
                    imageUrl:           "http://deltafonts.com/wp-content/uploads/s4c-logo.png",
                    backgroundImageUrl: "http://static.bbci.co.uk/tviplayer/1.121.0/img/emp/s4c_640.jpg",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio_video/simulcast/hls/uk/hls_tablet/ak/s4cpbs.m3u8",
            ],
            [
                    name:               "BBC One London",
                    genres:             "NEWS",
                    description:        "The latest stories making waves from the capital.",
                    imageUrl:           "http://www.triconestudios.com/wp-content/uploads/2012/04/bbc_london_640_360.jpg",
                    backgroundImageUrl: "http://vignette3.wikia.nocookie.net/logopedia/images/c/c2/BBC_One_London_Marathon_sting.png",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio_video/simulcast/hls/uk/hls_tablet/ak/bbc_one_london.m3u8",
            ],
            [
                    name:               "BBC One Scotland",
                    genres:             "NEWS",
                    description:        "Scottish variation of the UK-wide BBC One.",
                    imageUrl:           "https://www.scottishgolf.org/wp-content/uploads/BBCScotland-logo.jpg",
                    backgroundImageUrl: "https://upload.wikimedia.org/wikipedia/en/thumb/c/cd/BBC_Scotland_corporate_logo.svg/1280px-BBC_Scotland_corporate_logo.svg.png",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio_video/simulcast/hls/uk/abr_hdtv/ak/bbc_one_scotland_hd.m3u8",
            ],
            [
                    name:               "BBC One Northern Ireland",
                    genres:             "NEWS",
                    description:        "Northern Irish variation of the UK-wide BBC One.",
                    imageUrl:           "https://pbs.twimg.com/profile_images/550386928780980225/6yQROLCn_400x400.png",
                    backgroundImageUrl: "http://theident.gallery/misc/misc/bbc1-e16-1.jpg",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio_video/simulcast/hls/uk/abr_hdtv/ak/bbc_one_northern_ireland_hd.m3u8",
            ],
            [
                    name:               "BBC One Wales",
                    genres:             "NEWS",
                    description:        "Welsh variation of the UK-wide BBC One.",
                    imageUrl:           "https://theident.gallery/bbc1/BBC1W-2012-ID-SPECIAL-STDAVIDSDAY-1-4.jpg",
                    backgroundImageUrl: "http://vignette1.wikia.nocookie.net/logopedia/images/c/c6/BBC_One_Wales_Olympics_sting_2016_(Sports).png",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio_video/simulcast/hls/uk/abr_hdtv/ak/bbc_one_wales_hd.m3u8",
            ],
            [
                    name:               "BBC Two Scotland",
                    genres:             "ENTERTAINMENT",
                    description:        "Specialised programming aimed at Scottish viewers.",
                    imageUrl:           "http://ichef.bbci.co.uk/images/ic/406x228/p027brvs.jpg",
                    backgroundImageUrl: "https://upload.wikimedia.org/wikipedia/en/thumb/c/cd/BBC_Scotland_corporate_logo.svg/1280px-BBC_Scotland_corporate_logo.svg.png",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio_video/simulcast/hls/uk/hls_tablet/ak/bbc_two_scotland.m3u8",
            ],
            [
                    name:               "BBC Two Northern Ireland",
                    genres:             "ENTERTAINMENT",
                    description:        "Specialised programming aimed at Northern Irish viewers.",
                    imageUrl:           "http://ichef.bbci.co.uk/images/ic/406x228/p027brjz.jpg",
                    backgroundImageUrl: "https://upload.wikimedia.org/wikipedia/en/thumb/f/f0/BBC_Two_Northern_Ireland.svg/1280px-BBC_Two_Northern_Ireland.svg.png",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio_video/simulcast/hls/uk/hls_tablet/ak/bbc_two_northern_ireland_digital.m3u8",
            ],
            [
                    name:               "BBC Two Wales",
                    genres:             "ENTERTAINMENT",
                    description:        "Specialised programming aimed at Welsh viewers.",
                    imageUrl:           "http://ichef.bbci.co.uk/images/ic/406x228/p025zq55.jpg",
                    backgroundImageUrl: "https://upload.wikimedia.org/wikipedia/en/thumb/4/4a/BBC_Two_Wales_logo.svg/1280px-BBC_Two_Wales_logo.svg.png",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio_video/simulcast/hls/uk/hls_tablet/ak/bbc_two_wales_digital.m3u8",
            ],
    ]

    static final RADIO = [
            [
                    name:               "BBC Radio 1",
                    genres:             "MUSIC",
                    description:        "Home of the Official Chart, the Live Lounge and the world's greatest DJs.",
                    imageUrl:           "https://d1xfdkesnyyyqp.cloudfront.net/mix_artwork/c0d82b24640173eeef1b.small.png",
                    backgroundImageUrl: "https://tvadvertsongs.co.uk/wp-content/uploads/2015/04/Radio-1-Where-It-Begins-1080p-56.129.jpg",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio/simulcast/hls/nonuk/sbr_low/llnw/bbc_radio_one.m3u8",
            ],
            [
                    name:               "BBC Radio 1Xtra",
                    genres:             "MUSIC",
                    description:        "Home of Trevor Nelson, Charlie Sloth, A.Dot, DJ Target and Mistajam.",
                    imageUrl:           "https://thumbnailer.mixcloud.com/unsafe/300x300/extaudio/1/7/9/d/4a68-40a9-4c3e-8eba-28dd825be727.png",
                    backgroundImageUrl: "https://tvadvertsongs.co.uk/wp-content/uploads/2015/04/Radio-1-Where-It-Begins-1080p-56.129.jpg",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio/simulcast/hls/nonuk/sbr_low/llnw/bbc_1xtra.m3u8",
            ],
            [
                    name:               "BBC Radio 2",
                    genres:             "MUSIC",
                    description:        "Amazing music. Played by an amazing line-up. The home of great music, entertainment and documentaries.",
                    imageUrl:           "http://static.radio.de/images/broadcasts/88/e1/3244/c300.png",
                    backgroundImageUrl: "http://i.dailymail.co.uk/i/pix/2013/01/17/article-2263670-0645DB41000005DC-799_634x348.jpg",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio/simulcast/hls/nonuk/sbr_low/llnw/bbc_radio_two.m3u8",
            ],
            [
                    name:               "BBC Radio 3",
                    genres:             "MUSIC",
                    description:        "Classical music is its core.",
                    imageUrl:           "http://gramophoneproduction.s3-eu-west-1.amazonaws.com/s3fs-public/styles/6_columns_wide/public/BBC-Radio-3.jpg",
                    backgroundImageUrl: "http://www.m-magazine.co.uk/wp-content/uploads/2016/01/BBC-Radio-3-long.jpg",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio/simulcast/hls/nonuk/sbr_low/llnw/bbc_radio_three.m3u8",
            ],
            [
                    name:               "BBC Radio 4",
                    genres:             "NEWS",
                    description:        "Speech based news, current affairs and factual network.",
                    imageUrl:           "https://files.list.co.uk/images/festivals/2016/fringe/2016BBCBBCO-AZJ-300.jpg",
                    backgroundImageUrl: "https://i.ytimg.com/vi/di6MfysPI0Y/maxresdefault.jpg",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio/simulcast/hls/nonuk/sbr_low/llnw/bbc_radio_fourfm.m3u8",
            ],
            [
                    name:               "BBC Radio 4LW",
                    genres:             "NEWS",
                    description:        "Speech based news, current affairs and factual network.",
                    imageUrl:           "http://images.radio.orange.com/radios/large_bbc_radio_4_lw.png",
                    backgroundImageUrl: "https://i.ytimg.com/vi/di6MfysPI0Y/maxresdefault.jpg",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio/simulcast/hls/nonuk/sbr_low/llnw/bbc_radio_fourlw.m3u8",
            ],
            [
                    name:               "BBC Radio 4 Extra",
                    genres:             "COMEDY",
                    description:        "Broadcasting classic comedy, drama and features.",
                    imageUrl:           "https://pbs.twimg.com/profile_images/378800000552182284/75b9143d795f67b900de9a3247396251.jpeg",
                    backgroundImageUrl: "http://ichef.bbci.co.uk/news/1024/media/images/83121000/jpg/_83121766_bbcradio4extra.jpg",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio/simulcast/hls/nonuk/sbr_low/llnw/bbc_radio_four_extra.m3u8",
            ],
            [
                    name:               "BBC Radio 5 Live",
                    genres:             "SPORTS",
                    description:        "The best of 5 live's sport coverage, insight and analysis.",
                    imageUrl:           "http://static.radio.net/images/broadcasts/ac/45/3247/c300.png",
                    backgroundImageUrl: "https://ichef.bbci.co.uk/images/ic/1920x1080/p01l80mb.jpg",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio/simulcast/hls/uk/sbr_high/ak/bbc_radio_five_live.m3u8",
            ],
            [
                    name:               "BBC Radio 5 Live Sports Extra",
                    genres:             "SPORTS",
                    description:        "Live sports extra",
                    imageUrl:           "http://static.radio.net/images/broadcasts/0b/36/11931/c300.png",
                    backgroundImageUrl: "https://ichef.bbci.co.uk/images/ic/1200x675/p0381j5l.jpg",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio/simulcast/hls/uk/sbr_high/ak/bbc_radio_five_live_sports_extra.m3u8",
            ],
            [
                    name:               "BBC Radio 6 Music",
                    genres:             "MUSIC",
                    description:        "Brings together the iconic and groundbreaking music of the past 40 years.",
                    imageUrl:           "http://internationalradiofaces.com/wp-content/uploads/2016/04/bbc-radio6-300x300.jpg",
                    backgroundImageUrl: "http://esquireuk.cdnds.net/15/37/1600x800/1600x800-bbc-radio-6-music-43-jpg-33aa0f7a.jpg",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio/simulcast/hls/nonuk/sbr_low/llnw/bbc_6music.m3u8",
            ],
            [
                    name:               "BBC Asian Network",
                    genres:             "NEWS",
                    description:        "A national digital radio station providing speech and music appealing anyone interested in British Asian lifestyles.",
                    imageUrl:           "https://thumbnailer.mixcloud.com/unsafe/300x300/extaudio/f/f/c/2/6188-1067-4c71-9ee4-702b988e6256.png",
                    backgroundImageUrl: "http://ticketnews.eventim.co.uk/wp-content/uploads/2016/03/BBC-AN-LIVE-gradient-background-logo.jpg",
                    liveStreamUrl:      "http://a.files.bbci.co.uk/media/live/manifesto/audio/simulcast/hls/nonuk/sbr_low/llnw/bbc_asian_network.m3u8",
            ],
    ]

    def ParentPuppet mParent
    def boolean mIsTopLevel
    def String mName
    def String mDescription
    def String mImageUrl
    def String mBackgroundImageUrl
    def String mUrl
    def PuppetIterator mChildren

    BBCiPlayerPuppet() {
        this(null, true, "BBC iPlayer", "TV and Radio from BBC iPlayer", null, null, null)
    }

    BBCiPlayerPuppet(ParentPuppet parent, boolean isTopLevel, String name, String description, String imageUrl, String backgroundUrl, String url) {
        mParent = parent
        mIsTopLevel = isTopLevel
        mName = name
        mDescription = description
        mImageUrl = imageUrl
        mBackgroundImageUrl = backgroundUrl
        mUrl = url
    }

    void setChildren(PuppetIterator children) {
        mChildren = children
    }

    @Override
    int getVersionCode() {
        return VERSION_CODE
    }

    @Override
    SearchesPuppet getSearchProvider() {
        return new BBCiPlayerSearchesPuppet(this)
    }

    @Override
    int getFastlaneBackgroundColor() {
        return 0xFF000000
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFFF54897
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFFF54897
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFFF54897
    }

    @Override
    List<Map<String, String>> getLiveChannelsMetaData() {
        def list = []
        [CHANNELS, RADIO].each {
            it.each { source ->
                if (source.liveStreamUrl) {
                    list << [
                            name       : source.name,
                            description: source.description,
                            genres     : source.genres,
                            logo       : source.imageUrl,
                            url        : source.liveStreamUrl
                    ]
                }
            }
        }
        (1..24).each {
            def final String imageUrl = "https://pbs.twimg.com/profile_images/581471317272211456/bmZn02Sz.png"
            list << [
                    name       : "BBC Red Button " + it,
                    genres     : "SPORTS",
                    logo       : imageUrl,
                    url        : sprintf("http://a.files.bbci.co.uk/media/live/manifesto/audio_video/webcast/hls/uk/abr_hdtv/llnw/sport_stream_%02d.m3u8", it)
            ]
        }
        return list
    }

    @Override
    PuppetIterator getChildren() {
        if (mChildren != null) {
            return mChildren
        }
        mChildren = new BBCiPlayerPuppetIterator()
        if (mParent == null) {

            mChildren.add(new BBCiPlayerPuppet(this, false, "Most Popular", "Currently trending on BBC iPlayer", null, null, "http://www.bbc.co.uk/iplayer/group/most-popular"))

            def featuredChildren = new BBCiPlayerPuppet(this, true, "Featured", "On the front page", null, null, "http://www.bbc.co.uk/iplayer").getChildren()
            featuredChildren.each { c -> mChildren.add(c) }

            ParentPuppet showsPuppet = new BBCiPlayerPuppet(this, true, "Shows", "Recorded programming", null, null, null)
            PuppetIterator showsChildren = new BBCiPlayerPuppetIterator()
            CHANNELS.each { channel ->
                if (channel.url) {
                    showsChildren.add(new BBCiPlayerPuppet(showsPuppet, false, channel.name, channel.description, channel.imageUrl, channel.backgroundImageUrl, channel.url))
                }
            }
            showsPuppet.setChildren(showsChildren)
            mChildren.add(showsPuppet)

            ParentPuppet livePuppet = new BBCiPlayerPuppet(this, true, "Live", "BBC channels live", null, null, null)
            PuppetIterator liveChildren = new BBCiPlayerPuppetIterator()
            CHANNELS.each { channel ->
                if (channel.liveStreamUrl) {
                    liveChildren.add(new BBCiPlayerSourcesPuppet(livePuppet, channel.name, "Live", channel.description, channel.imageUrl, channel.backgroundImageUrl, true, channel.liveStreamUrl))
                }
            }
            livePuppet.setChildren(liveChildren)
            mChildren.add(livePuppet)

            ParentPuppet redButtonPuppet = new BBCiPlayerPuppet(this, true, "\tRed Button", "Interactive television services", null, null, null)
            PuppetIterator redButtonChildren = new BBCiPlayerPuppetIterator()
            (1..24).each {
                def final String imageUrl = "https://pbs.twimg.com/profile_images/581471317272211456/bmZn02Sz.png"
                def final String backgroundImageUrl = "http://studiomh.co.uk/wp-content/uploads/2015/06/bbc_red_button21.jpg"
                redButtonChildren.add(new BBCiPlayerSourcesPuppet(redButtonPuppet, "Red Button " + it, "Red Button", null, imageUrl, backgroundImageUrl, true, sprintf("http://a.files.bbci.co.uk/media/live/manifesto/audio_video/webcast/hls/uk/abr_hdtv/llnw/sport_stream_%02d.m3u8", it)))
                redButtonChildren.add(new BBCiPlayerSourcesPuppet(redButtonPuppet, "Red Button " + it + "b", "Red Button", null, imageUrl, backgroundImageUrl, true, sprintf("http://a.files.bbci.co.uk/media/live/manifesto/audio_video/webcast/hls/uk/abr_hdtv/llnw/sport_stream_%02db.m3u8", it)))
            }
            redButtonPuppet.setChildren(redButtonChildren)
            mChildren.add(redButtonPuppet)

            ParentPuppet radioPuppet = new BBCiPlayerPuppet(this, true, "\tRadio", "BBC radio stations", null, null, null)
            PuppetIterator radioChildren = new BBCiPlayerPuppetIterator()
            RADIO.each { radio ->
                if (radio.liveStreamUrl) {
                    radioChildren.add(new BBCiPlayerSourcesPuppet(radioPuppet, radio.name, "Radio", radio.description, radio.imageUrl, radio.backgroundImageUrl, true, radio.liveStreamUrl))
                }
            }
            radioPuppet.setChildren(radioChildren)
            mChildren.add(radioPuppet)
        }
        if (mUrl != null) {
            Document document = Jsoup.connect(mUrl).get()

            for (Element item : document.select(".list-item:not(.unavailable),.grid__item .single-item,.view-more-container")) {
                if (item.hasClass("view-more-container")) {
                    def Element relatedVideo = item.parent().firstElementSibling()
                    def String title = relatedVideo.select(".top-title").first().text()
                    def String description = relatedVideo.select(".master-brand .medium").first().text()
                    def String imageUrl = relatedVideo.select(".r-image").first().attr("data-ip-src")
                    def String imageId = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf("."))
                    imageUrl = "http://ichef.bbci.co.uk/images/ic/304x304/" + imageId + ".jpg"
                    def String backgroundImageUrl = "http://ichef.bbci.co.uk/images/ic/1248x702/" + imageId + ".jpg"
                    mChildren.add(new BBCiPlayerPuppet(this, false, title, description, imageUrl, backgroundImageUrl, item.absUrl("href")))
                } else if (item.hasClass("single-item")) {
                    mChildren.add(new BBCiPlayerSourcesPuppet(this, item.parent()))
                } else if (item.select(".view-more-container").first() == null) {
                    mChildren.add(new BBCiPlayerSourcesPuppet(this, item))
                }
            }

            Element paginate = document.select("div.paginate").first()
            if (paginate != null) {
                Element page = paginate.select(".page.focus").first()
                Element nextPage = page.nextElementSibling()
                if (nextPage != null) {
                    nextPage = nextPage.select("a").first()
                    Element lastPage = page.lastElementSibling().select("a").first()
                    String name = sprintf("Page %s/%s", nextPage.ownText().trim(), lastPage.ownText().trim())
                    mChildren.add(new BBCiPlayerPuppet(this, false, name, "Traverse the next page", null, null, nextPage.absUrl("href")))
                }
            }
        }
        return mChildren
    }

    @Override
    boolean isTopLevel() {
        return mIsTopLevel
    }

    @Override
    String getName() {
        return mName
    }

    @Override
    String getCategory() {
        return "Public Service"
    }

    @Override
    String getShortDescription() {
        return mDescription
    }

    @Override
    String getImageUrl() {
        return mImageUrl != null ? mImageUrl : "http://orig05.deviantart.net/a9ae/f/2013/347/0/1/bbc_iplayer_metro_by_d4rk_amethyst-d6xs5ii.png"
    }

    @Override
    String getBackgroundImageUrl() {
        return mBackgroundImageUrl != null ? mBackgroundImageUrl : "https://securethoughts.com/wp-content/uploads/2015/02/bbciplayer-2.jpeg"
    }

    @Override
    boolean isUnavailableIn(String region) {
        return false
    }

    @Override
    String getPreferredRegion() {
        return null
    }

    @Override
    int getShieldLevel() {
        return 0
    }

    @Override
    ParentPuppet getParent() {
        return mParent
    }

    @Override
    PuppetIterator getRelated() {
        return null
    }

    @Override
    public String toString() {
        return mParent == null ? getName() : mParent.toString() + " < " + getName()
    }

    def static class BBCiPlayerPuppetIterator extends PuppetIterator {

        def ArrayList<Puppet> mPuppets = new ArrayList<>()
        def int currentIndex = 0

        @Override
        boolean hasNext() {
            if (currentIndex < mPuppets.size()) {
                return true
            } else {
                currentIndex = 0 // Reset for reuse
            }
            return false
        }

        @Override
        void add(Puppet puppet) {
            mPuppets.add(puppet)
        }

        @Override
        Puppet next() {
            return mPuppets.get(currentIndex++)
        }

        @Override
        void remove() {
        }
    }

    def static class BBCiPlayerSearchesPuppet extends BBCiPlayerPuppet implements SearchesPuppet {

        static final String URL_TEMPLATE = "http://www.bbc.co.uk/iplayer/search?q="

        public BBCiPlayerSearchesPuppet(ParentPuppet parent) {
            super(parent, false, "Search", null, null, null, URL_TEMPLATE)
        }

        @Override
        public void setSearchQuery(String searchQuery) {
            mChildren = null
            mUrl = URL_TEMPLATE + URLEncoder.encode(searchQuery, "UTF-8")
        }
    }

    def static class BBCiPlayerSourcesPuppet implements SourcesPuppet {

        def ParentPuppet mParent

        def String mName
        def String mCategory
        def String mDescription
        def String mImageUrl
        def String mBackgroundImageUrl
        def boolean mIsLive = false
        def String mDirectUrl
        def String mPublicationDate
        def long mDuration = -1

        def String mUrl

        def List<SourcesPuppet.SubtitleDescription> mSubtitles = new ArrayList<SourcesPuppet.SubtitleDescription>()

        BBCiPlayerSourcesPuppet(parent, String name, String category, String description, String imageUrl, String backgroundImageUrl, isLive, String directUrl) {
            mParent = parent
            mName = name
            mCategory = category
            mDescription = description
            mImageUrl = imageUrl
            mBackgroundImageUrl = backgroundImageUrl
            mIsLive = isLive
            mDirectUrl  = directUrl
        }

        BBCiPlayerSourcesPuppet(parent, Element item) {
            mParent = parent

            mUrl = item.select("a.list-item-link,a.single-item").first().absUrl("href")

            mName = item.select(".title.top-title,.single-item__title").first().text().trim()
            try {
                def String subtitle = item.select(".subtitle,.single-item__subtitle").first().text().trim()
                mName += ": " + subtitle
            } catch (ignore) {
            }

            try {
                mCategory = item.select(".master-brand .small").first().text().trim()
            } catch (ignore) {
            }

            try {
                mDescription = item.select(".synopsis,.single-item__desc__label").first().text().trim()
            } catch (ignore) {
            }

            def String imageUrl = item.select(".r-image").first().attr("data-ip-src")
            def String imageId = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf("."))
            mImageUrl = "http://ichef.bbci.co.uk/images/ic/304x304/" + imageId + ".jpg"
            mBackgroundImageUrl = "http://ichef.bbci.co.uk/images/ic/1248x702/" + imageId + ".jpg"

            try {
                def String publishedOn = item.select(".release").first().text()
                mPublicationDate = publishedOn.replace("First shown: ", "").trim()
            } catch (ignore) {
            }

            try {
                def String duration = item.select("a.list-item-link,a.single-item").first().attr("data-duration")
                if (duration != "") {
                    mDuration = Long.parseLong(duration) * 1000
                }
            } catch (ignore) {
            }
        }

        @Override
        String getPublicationDate() {
            return mPublicationDate
        }

        @Override
        long getDuration() {
            return mDuration
        }

        @Override
        SourcesPuppet.SourceIterator getSources() {
            return new BBCiPlayerSourceIterator()
        }

        @Override
        boolean isLive() {
            return mIsLive
        }

        @Override
        List<SourcesPuppet.SubtitleDescription> getSubtitles() {
            return mSubtitles
        }

        @Override
        String getName() {
            return mName
        }

        @Override
        String getCategory() {
            return mCategory
        }

        @Override
        String getShortDescription() {
            return mDescription
        }

        @Override
        String getImageUrl() {
            return mImageUrl
        }

        @Override
        String getBackgroundImageUrl() {
            return mBackgroundImageUrl
        }

        @Override
        boolean isUnavailableIn(String region) {
            if (mDirectUrl != null &&
                    (mDirectUrl.contains("audio") && mDirectUrl.contains("sbr_low")) ||
                    mDirectUrl in ["http://bbcwshdlive01-lh.akamaihd.net/i/atv_1@61433/master.m3u8", "http://bbcwshdlive01-lh.akamaihd.net/i/ptv_1@78015/master.m3u8"]) {
                return false
            }
            return region != "uk"
        }

        @Override
        String getPreferredRegion() {
            return "uk"
        }

        @Override
        int getShieldLevel() {
            return 5
        }

        @Override
        ParentPuppet getParent() {
            return mParent
        }

        @Override
        PuppetIterator getRelated() {
            return mParent.getChildren()
        }

        @Override
        String toString() {
            return mParent == null ? getName() : mParent.toString() + " < " + getName()
        }

        def class BBCiPlayerSourceIterator implements SourcesPuppet.SourceIterator {

            static final String URL_TEMPLATE = "http://open.live.bbc.co.uk/mediaselector/5/select/version/2.0/mediaset/iptv-all/vpid/"

            def ArrayList<SourcesPuppet.SourceDescription> mSources = null
            def int currentIndex = 0

            @Override
            boolean hasNext() {
                if (mSources == null) {
                    mSources = new ArrayList<>()
                    if (BBCiPlayerSourcesPuppet.this.mDirectUrl) {
                        def SourcesPuppet.SourceDescription source = new SourcesPuppet.SourceDescription()
                        source.url = BBCiPlayerSourcesPuppet.this.mDirectUrl
                        mSources.add(source)
                    } else {
                        def String url = BBCiPlayerSourcesPuppet.this.mUrl
                        def String page = new URL(url).getText()
                        Matcher matcher = page =~ /"vpid":"(.+?)"/
                        if (matcher.find()) {
                            url = URL_TEMPLATE + matcher.group(1)
                            def org.w3c.dom.NodeList media
                            try {
                                def org.w3c.dom.Document root = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url)
                                media = root.getElementsByTagName("media")
                            } catch (Exception ex) {
                                return false
                            }
                            def urls = []
                            for (int i = 0; i < media.getLength(); i++) {
                                def org.w3c.dom.Element node = (org.w3c.dom.Element) media.item(i)
                                def org.w3c.dom.NodeList elements = node.getElementsByTagName("connection")
                                for (int j = 0; j < elements.getLength(); j++) {
                                    def org.w3c.dom.Element element = (org.w3c.dom.Element) elements.item(j)
                                    if (element.getAttribute("transferFormat") == "hls") {
                                        def String stream = element.getAttribute("href")
                                        if (!urls.contains(stream)) {
                                            def SourcesPuppet.SourceDescription source = new SourcesPuppet.SourceDescription()
                                            source.url = stream
                                            source.bitrate = Long.parseLong(node.getAttribute("bitrate"))
                                            source.width = Integer.parseInt(node.getAttribute("width"))
                                            source.height = Integer.parseInt(node.getAttribute("height"))
                                            mSources.add(source)
                                            if (element.getAttribute("supplier").contains("akamai")) {
                                                mSources << source
                                            } else {
                                                mSources.add(0, source) // Prioritize non-akamai links
                                            }
                                        }
                                    } else if (node.getAttribute("service") == "captions") {
                                        SourcesPuppet.SubtitleDescription subtitle = new SourcesPuppet.SubtitleDescription()
                                        subtitle.url = element.getAttribute("href")
                                        subtitle.mime = node.getAttribute("type")
                                        BBCiPlayerSourcesPuppet.this.mSubtitles.add(subtitle)
                                    }
                                }
                            }
                        }
                    }
                }
                return currentIndex < mSources.size()
            }

            @Override
            SourcesPuppet.SourceDescription next() {
                return mSources.get(currentIndex++)
            }

            @Override
            void remove() {
            }
        }
    }
}