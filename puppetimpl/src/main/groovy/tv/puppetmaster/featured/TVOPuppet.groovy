package tv.puppetmaster.featured

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import tv.puppetmaster.data.i.*
import tv.puppetmaster.data.i.Puppet.PuppetIterator

import java.util.concurrent.TimeUnit

def class TVOPuppet implements InstallablePuppet {

    static final int VERSION_CODE = 3

    @Override
    int getVersionCode() {
        return 3
    }

    @Override
    SearchesPuppet getSearchProvider() {
        return null
    }

    @Override
    int getFastlaneBackgroundColor() {
        return 0xFF000000
    }

    @Override
    int getSearchAffordanceBackgroundColor() {
        return 0xFF000000
    }

    @Override
    int getSelectedBackgroundColor() {
        return 0xFF000000
    }

    @Override
    int getPlayerBackgroundColor() {
        return 0xFF000000
    }

    @Override
    List<Map<String, String>> getLiveChannelsMetaData() {
        return null
    }

    @Override
    PuppetIterator getChildren() {
        TVOPuppetIterator children = new TVOPuppetIterator()
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz1".toUpperCase().toCharArray();
        for (char letter : alphabet) {
            children.add(new TVOAlphabeticalIndexPuppet(letter.toString()))
        }
        return children
    }

    @Override
    boolean isTopLevel() {
        return false
    }

    @Override
    String getName() {
        return "TVO"
    }

    @Override
    String getCategory() {
        return "Canada"
    }

    @Override
    String getShortDescription() {
        return "Catch-up on world class documentaries broadcast on TVO, the public broadcaster for Ontario, Canada."
    }

    @Override
    String getImageUrl() {
        return "http://storage.torontosun.com/v1/dynamic_resize/sws_path/suns-prod-images/1297703617749_ORIGINAL.jpg?quality=80&size=420x"
    }

    @Override
    String getBackgroundImageUrl() {
        return "http://post.queensu.ca/~leuprech/pics/TVO_think_red_RGB.jpg"
    }

    @Override
    boolean isAvailable(String region) {
        return true
    }

    @Override
    String[] preferredRegions() {
        return new String[0]
    }

    @Override
    int immigrationStricture() {
        return 0
    }

    @Override
    ParentPuppet getParent() {
        return null
    }

    @Override
    PuppetIterator getRelated() {
        return null
    }

    @Override
    String toString() {
        return getName()
    }

    def static class TVOPuppetIterator extends PuppetIterator {

        def ArrayList<Puppet> mList = new ArrayList<>()
        def int mCurrentIndex = 0

        @Override
        void add(Puppet puppet) {
            mList.add(puppet)
        }

        @Override
        boolean hasNext() {
            return mCurrentIndex < mList.size()
        }

        @Override
        Puppet next() {
            return (Puppet) mList.get(mCurrentIndex++)
        }
    }

    def class TVOAlphabeticalIndexPuppet implements ParentPuppet {

        def String mCharacter

        TVOAlphabeticalIndexPuppet(String character) {
            mCharacter = character
        }

        @Override
        PuppetIterator getChildren() {
            Document document = Jsoup.connect("http://tvo.org/programs/" + mCharacter).get()
            Elements anchors = document.select('ul.program-list a')
            TVOPuppetIterator children = new TVOPuppetIterator()
            for (Element a : anchors) {
                children.add(new TVOFinalDirectoryPuppet(a))
            }
            return children
        }

        @Override
        boolean isTopLevel() {
            return false
        }

        @Override
        String getName() {
            return mCharacter
        }

        @Override
        String getCategory() {
            return null
        }

        @Override
        String getShortDescription() {
            return null
        }

        @Override
        String getImageUrl() {
            return TVOPuppet.this.getImageUrl()
        }

        @Override
        String getBackgroundImageUrl() {
            return TVOPuppet.this.getBackgroundImageUrl()
        }

        @Override
        boolean isAvailable(String region) {
            return true
        }

        @Override
        String[] preferredRegions() {
            return new String[0]
        }

        @Override
        int immigrationStricture() {
            return 0
        }

        @Override
        ParentPuppet getParent() {
            return TVOPuppet.this
        }

        @Override
        PuppetIterator getRelated() {
            return null
        }

        @Override
        String toString() {
            return getName()
        }

        def class TVOFinalDirectoryPuppet implements ParentPuppet {

            Element mA

            TVOFinalDirectoryPuppet(Element a) {
                mA = a
            }

            @Override
            PuppetIterator getChildren() {
                Document document = Jsoup.connect(mA.absUrl("href")).get()
                Elements divs = document.select('.views-row')
                TVOPuppetIterator children = new TVOPuppetIterator()
                for (Element div : divs) {
                    children.add(new TVOScrapeSourcesPuppet(div))
                }
                return children
            }

            @Override
            boolean isTopLevel() {
                return false
            }

            @Override
            String getName() {
                return mA.text().trim()
            }

            @Override
            String getCategory() {
                return null
            }

            @Override
            String getShortDescription() {
                return null
            }

            @Override
            String getImageUrl() {
                return TVOAlphabeticalIndexPuppet.this.getImageUrl()
            }

            @Override
            String getBackgroundImageUrl() {
                return TVOAlphabeticalIndexPuppet.this.getBackgroundImageUrl()
            }

            @Override
            boolean isAvailable(String region) {
                return true
            }

            @Override
            String[] preferredRegions() {
                return new String[0]
            }

            @Override
            int immigrationStricture() {
                return 0
            }

            @Override
            ParentPuppet getParent() {
                return TVOAlphabeticalIndexPuppet.this
            }

            @Override
            PuppetIterator getRelated() {
                return null
            }

            @Override
            String toString() {
                return getName()
            }

            def class TVOScrapeSourcesPuppet implements SourcesPuppet {

                def Element mDiv

                TVOScrapeSourcesPuppet(Element div) {
                    mDiv = div
                }

                @Override
                String getPublicationDate() {
                    return mDiv.select('.date-display-single').first().text()
                }

                @Override
                long getDuration() {
                    String duration = mDiv.select('.date-display-single').first().nextElementSibling().text().replace("-", "").trim()
                    return convertDuration(duration)
                }

                @Override
                SourcesPuppet.SourceIterator getSources() {
                    return new TVOSourcesIterator(mDiv.select('.views-field-title a').first().absUrl('href'))
                }

                @Override
                boolean isLive() {
                    return false
                }

                @Override
                List<SourcesPuppet.SubtitleDescription> getSubtitles() {
                    return null
                }

                @Override
                String getName() {
                    return mDiv.select('.video-container h1').first().text()
                }

                @Override
                String getCategory() {
                    return null
                }

                @Override
                String getShortDescription() {
                    return mDiv.select('.field-name-field-description').first().text()
                }

                @Override
                String getImageUrl() {
                    return mDiv.select('.views-field-brightcove-image-field img').first().absUrl('src')
                }

                @Override
                String getBackgroundImageUrl() {
                    return mDiv.select('.views-field-brightcove-image-field img').first().absUrl('src')
                }

                @Override
                boolean isAvailable(String region) {
                    return true
                }

                @Override
                String[] preferredRegions() {
                    return new String[0]
                }

                @Override
                int immigrationStricture() {
                    return 0
                }

                @Override
                ParentPuppet getParent() {
                    return TVOFinalDirectoryPuppet.this
                }

                @Override
                PuppetIterator getRelated() {
                    return TVOFinalDirectoryPuppet.this.getChildren()
                }

                @Override
                String toString() {
                    return getName()
                }

                private static long convertDuration(String str) {  // HH:MM[:SS] to milliseconds
                    if (str.equals("")) {
                        return -1
                    }

                    String[] data = str.split(":")

                    int time
                    if (data.length > 2) {
                        int hours  = Integer.parseInt(data[0])
                        int minutes = Integer.parseInt(data[1])
                        int seconds = Integer.parseInt(data[2])
                        time = seconds + 60 * minutes + 3600 * hours
                    } else if (data.length > 1) {
                        int minutes = Integer.parseInt(data[0])
                        int seconds = Integer.parseInt(data[1])
                        time = seconds + 60 * minutes
                    } else {
                        int seconds = Integer.parseInt(data[0])
                        time = seconds
                    }

                    return TimeUnit.MILLISECONDS.convert(time, TimeUnit.SECONDS)
                }

                def class TVOSourcesIterator implements SourcesPuppet.SourceIterator {

                    String mUrl
                    boolean mUnseen = true

                    TVOSourcesIterator(String url) {
                        mUrl = url
                    }

                    @Override
                    boolean hasNext() {
                        return mUnseen
                    }

                    @Override
                    SourcesPuppet.SourceDescription next() {
                        mUnseen = false

                        Document document = Jsoup.connect(mUrl).get()

                        String format = 'http://c.brightcove.com/services/mobile/streaming/index/rendition.m3u8?assetId=%s&pubId=%s&videoId=%s'
                        Element video = document.select('video#TVOvideo').first()
                        String videoId = video.attr('data-video-id')
                        String account = video.attr('data-account')

                        SourcesPuppet.SourceDescription sourceDescription = new SourcesPuppet.SourceDescription()
                        sourceDescription.url = String.format(format, videoId, account, videoId)
                        return sourceDescription
                    }
                }
            }
        }
    }
}