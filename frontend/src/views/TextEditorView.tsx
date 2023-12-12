import React from "react";
import { useParams } from "react-router-dom";
import TextEditor from "../components/TextEditor";
import Comment from "../components/Comment";
import styled from "styled-components";

interface TextEditorViewProps {}

const StyledTextEditorView = styled.section`
  display: flex;
  justify-content: center;
`;

const TextEditorView: React.FC<TextEditorViewProps> = () => {
  const { id } = useParams<{ id: string }>();

  if (!id) {
    // id가 없을 때의 처리
    return <div>No ID provided</div>;
  }


  return(
    <StyledTextEditorView>
      <TextEditor id={id} />
    </StyledTextEditorView>
  )
}

export default TextEditorView;