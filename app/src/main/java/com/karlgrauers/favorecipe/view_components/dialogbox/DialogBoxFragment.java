package com.karlgrauers.favorecipe.view_components.dialogbox;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


/*
 * Klass för att skapa dialogrutor. Ärver av interna klassen
 * DialogFragment och använder en egenskriven klicklyssnare,
 * 'OnDialogClickListener'.
 * Inspirerad av följande implementation: https://stackoverflow.com/questions/23408756/create-a-general-class-for-custom-dialog-in-java-android
 */



public class DialogBoxFragment extends DialogFragment {
    private final static String TITLE = "dialog_title", MESSAGE = "dialog_message", HAS_CANCEL_BUTTON = "cancel_button";
    private OnDialogClickListener dialogClickListener;


    /**
     * Statisk konstruktor. Instansierar dialogruta
     * och lägger värden i form av titel, text
     * och antal knappar (ok och cancel eller endast ok) i
     * ett bundle som tas emot i metod 'onCreateDialog'
     * @param title innehåller den titel dialogrutan ska ha.
     * @param message innehåller texten dialogrutan ska ha.
     * @param hasCancelButton, true om dialogruta ska ha
     *                         både ok och cancel, annars false.
     * @param dialogClickListener den klass som ska implementera
     *                            klicklyssnaren.
     */
    public static DialogBoxFragment newInstance(String title, String message, boolean hasCancelButton, OnDialogClickListener dialogClickListener) {

        DialogBoxFragment dialogBoxFragment = new DialogBoxFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(MESSAGE, message);
        args.putBoolean(HAS_CANCEL_BUTTON, hasCancelButton);
        dialogBoxFragment.setArguments(args);
        dialogBoxFragment.dialogClickListener = dialogClickListener;
        return dialogBoxFragment;
    }



    /**
     * Skapar dialogruta. Hämtar titel, text och boolean
     * för knappar från bundle som skapats i konstruktor.
     * Sätter klicklyssnare på knappar.
     * @param savedInstanceState innehåller instansdata om sådan är
     *                           tillgänglig, annars null.
     * @return den färdigskapade dialogrutan.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        assert getArguments() != null;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle(getArguments().getString(TITLE))
                .setMessage(getArguments().getString(MESSAGE))
                .setCancelable(false)
                .setPositiveButton(getString(android.R.string.ok),
                        (dialogInterface, whichButton) -> dialogClickListener.onDialogOkClicked(dialogInterface)
                );

        if(getArguments().getBoolean(HAS_CANCEL_BUTTON)) {
            builder.setNegativeButton(getString(android.R.string.cancel),
                    (dialogInterface, whichButton) -> dialogClickListener.onDialogCancelClicked(dialogInterface)
            );
        }
        return builder.create();
    }
}
