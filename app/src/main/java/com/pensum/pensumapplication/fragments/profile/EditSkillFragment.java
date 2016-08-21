package com.pensum.pensumapplication.fragments.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.models.Skill;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by eddietseng on 8/21/16.
 */
public class EditSkillFragment extends DialogFragment {
    @BindView(R.id.etSkillName)EditText etSkillName;
    @BindView(R.id.radio_exp_group) RadioGroup expGroup;
//    @BindView(R.id.btnSkillSave) RadioGroup btnSkillSave;

    private Unbinder unbinder;

    public interface EditSkillFragmentListener {
        void onFinishEditDialog(Skill skill);
    }

    public EditSkillFragment() {

    }

    public static EditSkillFragment newInstance(String title, Skill skill) {
        EditSkillFragment frag = new EditSkillFragment();
        Bundle args = new Bundle();
        args.putString("title", title);

        if(skill != null) {
            args.putString("name",skill.getSkillName());
            args.putInt("experience",skill.getSkillExperiences());
        }
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_edit_skill, container);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Add Skill");
        getDialog().setTitle(title);

        String skillName = getArguments().getString("name");
        if(skillName!=null)
            etSkillName.setText(skillName);

        int exp = getArguments().getInt("experience");
        if(exp!=0) {
            switch (exp) {
                case 1:
                    expGroup.check(R.id.rbLessOne);
                    break;
                case 2:
                    expGroup.check(R.id.rbOneToTwo);
                    break;
                case 3:
                    expGroup.check(R.id.rbTwoToFive);
                    break;
                case 4:
                    expGroup.check(R.id.rbFiveToTen);
                    break;
                case 5:
                    expGroup.check(R.id.rbTenToTwenty);
                    break;
                case 6:
                    expGroup.check(R.id.rbTwentyPlus);
                    break;
                default:
                    Log.d("Exp rg","Can't handle experience value: " + exp);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnSkillSave)
    public void saveSkill(Button button) {
        if(etSkillName.length() > 0 && expGroup.getCheckedRadioButtonId() != -1) {
            Skill skill = new Skill();
            skill.setSkillName(etSkillName.getText().toString());

            int exp = 0;
            switch (expGroup.getCheckedRadioButtonId()) {
                case R.id.rbLessOne:
                    exp = 1;
                    break;
                case R.id.rbOneToTwo:
                    exp = 2;
                    break;
                case R.id.rbTwoToFive:
                    exp = 3;
                    break;
                case R.id.rbFiveToTen:
                    exp = 4;
                    break;
                case R.id.rbTenToTwenty:
                    exp = 5;
                    break;
                case R.id.rbTwentyPlus:
                    exp = 6;
                    break;
                default:
                    Log.d("Exp rg","Can't handle radio button ID: "
                            + expGroup.getCheckedRadioButtonId());
            }
            skill.setSkillExperiences(exp);

            EditSkillFragmentListener listener = (EditSkillFragmentListener)getParentFragment();
            listener.onFinishEditDialog(skill);
        } else {
            Toast.makeText(getContext(),"Error Saving Skill",Toast.LENGTH_SHORT).show();
        }

        // Close the dialog and return back to the parent
        dismiss();
    }

    @OnClick(R.id.btnCloseEditSkill)
    public void exitEditSkill(ImageButton button) {
        dismiss();
    }
}
