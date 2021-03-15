package rawquestiontransformation;

/**
 * This class is used to perform the preliminary transformation of online-sourced multiple-choice questions, in order to
 * make them usable by this system.
 *
 * An example of a raw question:
 * 
 * The first milk produced by a woman in the first few days after giving birth is called:
 * 
 * 0 formula
 * 
 * 0 enrichment
 * 
 * 1 colostrum
 * 
 * 0 sputum
 * 
 * 0 amniocentesis
 * 
 * The answer options must be turned into A-D (deleting one of the incorrect ones if > 4) and recording the correct
 * answer option, represented by '1' in this case.
 *
 * @author Sam Barba
 */
public class RawQuestionTransformer {

}
