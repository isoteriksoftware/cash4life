package com.isoterik.cash4life.iqtest.components

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.utils.Array
import com.isoterik.cash4life.GlobalConstants
import com.isoterik.cash4life.GlobalUtil
import com.isoterik.cash4life.UserManager
import com.isoterik.cash4life.iqtest.IQTestSplash
import com.isoterik.cash4life.iqtest.utils.Sentence
import io.github.isoteriktech.xgdx.Component
import io.github.isoteriktech.xgdx.GameObject
import io.github.isoteriktech.xgdx.Transform
import io.github.isoteriktech.xgdx.XGdx
import io.github.isoteriktech.xgdx.x2d.components.renderer.SpriteRenderer
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitionDirection
import io.github.isoteriktech.xgdx.x2d.scenes.transition.SceneTransitions
import java.util.*
import kotlin.collections.ArrayList

class SentenceGameManager : Component() {
    private val whiteLettersAtlas = XGdx.instance().assets.getAtlas(
        GlobalConstants.IQ_TEST_ASSETS_HOME + "/spritesheets/white.atlas"
    )
    private val blackLettersAtlas = XGdx.instance().assets.getAtlas(
        GlobalConstants.IQ_TEST_ASSETS_HOME + "/spritesheets/black.atlas"
    )

    private val REWARD_AMOUNT = 10000f

    var sentences = ArrayList<Sentence.Category>()

    lateinit var xGdx: XGdx

    private lateinit var uiManager: UIManager
    private lateinit var sentenceManager: SentenceManager

    private var missingOptionTransforms = Array<Transform>()
    private var wordsFromMissingOptionsTransforms = Array<Array<Transform>>()

    fun init() {
        uiManager = scene.findGameObject("uiManager").getComponent(UIManager::class.java)
        sentenceManager = scene.findGameObject("sentenceManager").getComponent(SentenceManager::class.java)
    }

    fun startLevel() {
        if (sentences.size <= 0) {
            uiManager.gameFinished()
            scene.findGameObject("userManager").getComponent(UserManager::class.java).deposit(REWARD_AMOUNT)
            return
        }

        clearScreen()

        uiManager.updateLevelText()

        val sentenceToFind = sentences.removeAt(0)

        val sentence = sentenceToFind.sentence
        val missingWords = sentenceToFind.missing
        val options = sentenceToFind.options

        uiManager.setTaps(missingWords.size)
        sentenceManager.missingWords = arrayFromArrayList(missingWords)

        val strippedSentenceGameObjects = createStrippedSentenceGameObjects(sentence)
        val strippedSentenceTransforms = getTransformsFromGameObjects(strippedSentenceGameObjects)

        sentenceManager.missingOptionTransforms = missingOptionTransforms
        sentenceManager.wordFromMissingOptionsTransforms = wordsFromMissingOptionsTransforms

        val optionsCombinedGameObjects = createCombinedOptionsGameObjects(missingWords, options)
        val optionsCombinedTransforms = getTransformsFromGameObjectsArrayed(optionsCombinedGameObjects)

        sentenceManager.optionsGameObjects = optionsCombinedGameObjects

        val singleOptionsGameObjects = asSingleArray(optionsCombinedGameObjects)
        val singleOptionsTransforms = asSingleArrayArrayed(optionsCombinedTransforms)

        resizeTransforms(strippedSentenceTransforms, 0.1f)
        val yPosition = (scene.gameWorldUnits.worldHeight - strippedSentenceTransforms[0].height) / 2f
        horizontalDistributeEvenly(strippedSentenceTransforms, 5f)

        GlobalUtil.resizeTransforms(singleOptionsTransforms, 0.1f)
        horizontalDistributeEvenly(singleOptionsTransforms, 1.5f)

        addGameObjects(strippedSentenceGameObjects)
        addGameObjects(singleOptionsGameObjects)
    }

    private fun arrayFromArrayList(arrayList: ArrayList<String>): Array<String> {
        val array = Array<String>()
        for (list in arrayList) array.add(list)
        return array
    }

    fun clearScreen() {
        removeGameObjects(scene.findGameObjects("missing"))
        removeGameObjects(scene.findGameObjects("blackLetter"))
        removeGameObjects(scene.findGameObjects("whiteLetter"))
    }

    private fun removeGameObjects(gameObjects: Array<GameObject>) {
        for (gameObject in gameObjects)
            removeGameObject(gameObject)
    }

    private fun addGameObjects(gameObjects: Array<GameObject>) {
        for (gameObject in gameObjects)
            addGameObject(gameObject)
    }

    private fun createStrippedSentenceGameObjects(strippedWord: String): Array<GameObject> {
        val gameObjects = Array<GameObject>()
        for (letter in strippedWord) {
            if (letter == '_') {

                val missingGameObject = scene.newSpriteObject(xGdx.assets.getTexture(
                    "${GlobalConstants.IQ_TEST_ASSETS_HOME}/images/fill_black.png"
                ))
                missingGameObject.transform.setSize(0.3f, 0.3f)
                missingGameObject.tag = "missing"
                missingOptionTransforms.add(missingGameObject.transform)
                gameObjects.add(missingGameObject)

                wordsFromMissingOptionsTransforms.add(Array())
                for (wordFromArray in wordsFromMissingOptionsTransforms) {
                    if (wordFromArray.size > 0) wordFromArray.add(missingGameObject.transform)
                }

                continue
            }
            val letterGameObject = createLetterGameObject(letter, blackLettersAtlas, false)
            if (letter == ' ') letterGameObject.getComponent(SpriteRenderer::class.java).isVisible = false

            gameObjects.add(letterGameObject)

            for (wordFromArray in wordsFromMissingOptionsTransforms) {
                wordFromArray.add(letterGameObject.transform)
            }
        }

        return gameObjects
    }

    private fun createCombinedOptionsGameObjects(missingWords: ArrayList<String>, options: ArrayList<String>): Array<Array<GameObject>> {
        val result = Array<GameObject>()

        for (missing in missingWords) {
            val neighbours = Array<GameObject>()
            for (char in missing) {
                val gameObject = createLetterGameObject(char, whiteLettersAtlas, true)
                gameObject.addComponent(SentenceOption())
                neighbours.add(gameObject)
                result.add(gameObject)
            }

            var i = 0
            while (i < neighbours.size) {
                var j = 0
                val component = neighbours[i].getComponent(SentenceOption::class.java)
                val size = neighbours.size
                while (j < size) {
                    component.neighbours.add(neighbours[j])
                    j++
                }
                component.word = missing
                i++
            }

            val gameObject = createLetterGameObject(' ', whiteLettersAtlas, true)
            gameObject.getComponent(SpriteRenderer::class.java).isVisible = false
            result.add(gameObject)
        }

        for (option in options) {
            val neighbours = Array<GameObject>()
            for (char in option) {
                val gameObject = createLetterGameObject(char, whiteLettersAtlas, true)
                gameObject.addComponent(SentenceOption())
                neighbours.add(gameObject)
                result.add(gameObject)
            }

            var i = 0
            while (i < neighbours.size) {
                var j = 0
                val component = neighbours[i].getComponent(SentenceOption::class.java)
                val size = neighbours.size
                while (j < size) {
                    component.neighbours.add(neighbours[j])
                    j++
                }
                component.word = option
                i++
            }

            val gameObject = createLetterGameObject(' ', whiteLettersAtlas, true)
            gameObject.getComponent(SpriteRenderer::class.java).isVisible = false
            result.add(gameObject)
        }

        return shuffleOptions(result)
    }

    private fun shuffleOptions(targets: Array<GameObject>): Array<Array<GameObject>> {
        val grouped = Array<Array<GameObject>>()
        var tmp = Array<GameObject>()
        for (target in targets) {
            if (target.getComponent(SpriteRenderer::class.java).isVisible) {
                tmp.add(target)
            }
            else {
                grouped.add(tmp)
                tmp = Array()
            }
        }

        grouped.shuffle()

        val size = grouped.size
        var index = 1
        while (index < (size * 2 - 1)) {
            val empty = createLetterGameObject(' ', whiteLettersAtlas, true)
            empty.getComponent(SpriteRenderer::class.java).isVisible = false
            val tmp = Array<GameObject>()
            tmp.add(empty)
            grouped.insert(index, tmp)
            index += 2
        }
        return grouped
    }

    private fun createLetterGameObject(letter: Char, lettersAtlas: TextureAtlas, isWhite: Boolean): GameObject {
        val keyword = if (letter == ' ') "empty" else letter.toString()

        val gameObject = scene.newActorSpriteObject(lettersAtlas.findRegion(keyword.toUpperCase(Locale.ROOT)))
        gameObject.tag = if (isWhite) "whiteLetter" else "blackLetter"

        return gameObject
    }

    private fun getTransformsFromGameObjects(gameObjects: Array<GameObject>): Array<Transform> {
        val transforms = Array<Transform>()
        for (gameObject in gameObjects)
            transforms.add(gameObject.transform)

        return transforms
    }

    private fun getTransformsFromGameObjectsArrayed(gameObjects: Array<Array<GameObject>>): Array<Array<Transform>> {
        val transforms = Array<Array<Transform>>()
        for (array in gameObjects) {
            val tmp = Array<Transform>()
            for (gameObject in array) {
                tmp.add(gameObject.transform)
            }
            transforms.add(tmp)
        }

        return transforms
    }

    private fun addComponents(gameObjects: Array<GameObject>, componentType: Class<out Any>) {
        for (gameObject in gameObjects) {
            gameObject.addComponent(componentType.newInstance() as Component?)
        }
    }

    private fun asSingleArray(arrays: Array<Array<GameObject>>): Array<GameObject> {
        val result = Array<GameObject>()
        for (array in arrays) {
            for (gameObject in array) {
                result.add(gameObject)
            }
        }

        return result
    }

    private fun asSingleArrayArrayed(arrays: Array<Array<Transform>>): Array<Transform> {
        val result = Array<Transform>()
        for (array in arrays) {
            for (transform in array) {
                result.add(transform)
            }
        }

        return result
    }

    private fun horizontalDistributeEvenly(transforms: Array<Transform>, yPosition: Float) {
        if (transforms.isEmpty) return

        val worldWidth = scene.gameWorldUnits.worldWidth
        val transformWidth = transforms[0].width
        val transformHeight = transforms[0].height

        var xPos = 0.0f
        var yPos = yPosition

        val groupedTransforms = Stack<Transform>()
        for (transform in transforms) {
            if (transform.gameObject.getComponent(SpriteRenderer::class.java).isVisible) {
                groupedTransforms.push(transform)
                transform.setPosition(xPos, yPos)
                xPos += transformWidth
                if (transform.x > worldWidth) {
                    xPos = 0.0f
                    yPos -= (transformHeight * 1.5f)

                    for (gt in groupedTransforms) {
                        gt.setPosition(xPos, yPos)
                        xPos += transformWidth
                    }
                    groupedTransforms.clear()
                }
            }
            else {
                xPos += transformWidth
                groupedTransforms.clear()
            }
        }
    }

    private fun resizeTransforms(transforms: Array<Transform>, magnitude: Float) {
        for (transform in transforms) {
            if (transform.gameObject.tag == "missing") continue
            val size = transform.size
            transform.setSize(size.y * magnitude, size.y * magnitude)
        }
    }
}