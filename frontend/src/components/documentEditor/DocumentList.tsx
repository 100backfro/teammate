import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";
import axiosInstance from "../../axios";

const API_BASE_URL = "http://118.67.128.124:8080";

const DocumentContainer = styled.div`
  box-sizing: border-box;
  width: 1024px;
  height: auto;
  display: flex;
  align-content: space-between;
  flex-wrap: wrap;
  margin-bottom: 20px;
`;

const DocumentItem = styled.div`
  width: 100%;
  border: 1px solid black;
  display: flex;
  justify-content: space-between;
  margin: 4px 0;
  padding: 10px;
  border-radius: 12px;
`;

const TitleContentContainer = styled.div`
  flex-grow: 1;
`;

const StyledButton = styled.button`
  background-color: rgb(163, 204, 163);
  color: #333333;
  border-radius: 0.5rem;
  margin: 4px;
`;

const TitleDaytime = styled.p`
  margin: 4px;
  align-self: end;
`;

const DatesContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  min-width: 150px;
`;

const Container = styled.section`
  min-height: 800px;
`;

const InputAndButton = styled.div`
  display: flex;
  align-items: center;
`;

const ButtonContainer = styled.div`
  width: 100%;
  display: flex;
  justify-content: space-between;
`;

const SearchInput = styled.input`
  background-color: white;
  width: 100%;
  height: 28px;
  color: black;
  font-size: 16px;
  border-radius: 8px;
`;

type Document = {
  id: string;
  title: string;
  content: string;
  teamId: string;
  commentsId: string[];
  createdDt: string;
  updatedDt: string;
};

type DocumentListProps = {
  teamId: number;
};

const DocumentList: React.FC<DocumentListProps> = ({ teamId }) => {
  const [documents, setDocuments] = useState<Document[]>([]);
  const [filteredDocuments, setFilteredDocuments] = useState<Document[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [currentPage, setCurrentPage] = useState(0);
  const pageSize = 10;
  const [Id, setId] = useState<number>(teamId);

  useEffect(() => {
    const fetchDocuments = async () => {
      try {
        const response = await axiosInstance.get(
          `${API_BASE_URL}/team/${Id}/documents`,
        );
        console.log("Response data:", response.data);

        // 'content' 필드에서 문서 배열을 추출하여 상태 업데이트
        if (response.data && Array.isArray(response.data.content)) {
          setDocuments(response.data.content);
          setFilteredDocuments(response.data.content);
        } else {
          // 응답에 'content' 배열이 없는 경우
          console.error("Invalid response structure:", response.data);
        }
      } catch (error) {
        console.error("Error fetching data: ", error);
      }
    };

    fetchDocuments();
  }, [currentPage, teamId]);

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(event.target.value);
  };

  const renderPagination = () => {
    const totalPages = Math.ceil(filteredDocuments.length / pageSize);

    let pages = [];
    for (let i = 0; i < totalPages; i++) {
      pages.push(
        <button key={i} onClick={() => handlePageChange(i)}>
          {i + 1}
        </button>,
      );
    }
    return <div>{pages}</div>;
  };

  const currentDocuments = filteredDocuments.slice(
    currentPage * pageSize,
    (currentPage + 1) * pageSize,
  );

  console.log(currentDocuments);

  const navigate = useNavigate();

  const handleDocumentClick = (id: string) => {
    navigate(`/team/${teamId}/documents/${id}`);
  };

  const handleDocumentCreate = () => {
    navigate(`/team/${teamId}/documents`);
  };

  return (
    <Container>
      <InputAndButton>
        <SearchInput
          type="text"
          placeholder="제목, 내용 검색"
          value={searchTerm}
          onChange={handleSearchChange}
        />
        <ButtonContainer>
          <StyledButton onClick={handleDocumentCreate}>문서 작성</StyledButton>
          <StyledButton>캘린더</StyledButton>
        </ButtonContainer>
      </InputAndButton>
      <DocumentContainer>
        {currentDocuments.length !== 0 ? (
          currentDocuments.map(
            (doc) => (
              console.warn(doc.id),
              (
                <DocumentItem
                  key={doc.id}
                  onClick={() => handleDocumentClick(doc.id)}
                >
                  <TitleContentContainer>
                    <h2>{doc.title}</h2>
                    <p>
                      {doc.content.length < 20
                        ? doc.content
                        : doc.content.substring(20) + "..."}
                    </p>
                  </TitleContentContainer>
                  <DatesContainer>
                    <TitleDaytime>Created: {doc.createdDt}</TitleDaytime>
                    <TitleDaytime>Updated: {doc.updatedDt}</TitleDaytime>
                  </DatesContainer>
                </DocumentItem>
              )
            ),
          )
        ) : (
          <span>문서가 없습니다.</span>
        )}
      </DocumentContainer>
      {renderPagination()}
    </Container>
  );
};

export default DocumentList;
