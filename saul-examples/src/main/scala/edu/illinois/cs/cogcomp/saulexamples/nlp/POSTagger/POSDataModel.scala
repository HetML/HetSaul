package edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.POSTagger.POSClassifiers.POSClassifier

object POSDataModel extends DataModel {

  val tokens = node[Constituent]

  val posLabel = property[Constituent]("label") {
    x: Constituent => x.getTextAnnotation.getView(ViewNames.POS).getConstituentsCovering(x).get(0).getLabel
  }

  val previousTag = property[Constituent]("pTag") {
    x: Constituent =>
    {
      "tagValue" + POSClassifier.classifier.discreteValue(tokens.getWithWindow(x, -1, 0))
    }
    // this feature in theory should give us the pos tag of the previous word,
  }
  val wordFormFeature = property[Constituent]("wordForm") {
    x: Constituent =>
      val wordForm = x.getTextAnnotation.getView(ViewNames.TOKENS).getConstituentsCovering(x).get(0).getLabel

      if (wordForm.length == 1 && "([{".indexOf(wordForm) != -1) {
        "-LRB-"
      } else if (wordForm.length == 1 && ")]}".indexOf(wordForm) != -1) {
        "-RRB-"
      } else {
        wordForm
      }
  }
}
