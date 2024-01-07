import React, { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";
import axiosInstance from "../../axios";
import "flatpickr/dist/flatpickr.min.css";
import flatpickr from "flatpickr";
import { Instance } from "flatpickr/dist/types/instance";

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
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 10;
  const navigate = useNavigate();
  const [totalPages, setTotalPages] = useState<number>(0);
  const datepickerRef = useRef<HTMLInputElement>(null);
  const [startDt, setStartDt] = useState<string>("1000-01-01");
  const [endDt, setEndDt] = useState<string>("3000-12-30");

  useEffect(() => {
    const fetchDocuments = async () => {
      try {
        const response = await axiosInstance.get(
          `/team/${teamId}/documents?sortBy=createdDt-desc`,
        );

        if (response.data && Array.isArray(response.data)) {
          const sortedDocuments = response.data.sort(
            (a, b) =>
              new Date(b.createdDt).getTime() - new Date(a.createdDt).getTime(),
          );
          setDocuments(sortedDocuments);
        } else {
          console.error("Invalid response structure:", response.data);
        }
      } catch (error) {
        console.error("Error fetching data: ", error);
      }
    };

    if (teamId) {
      fetchDocuments();
    }
  }, [teamId]);

  useEffect(() => {
    const filterDocumentsByDate = () => {
      const startDate = new Date(startDt);
      const endDate = new Date(endDt);

      return documents.filter((doc) => {
        const docDate = new Date(doc.createdDt);
        return docDate >= startDate && docDate <= endDate;
      });
    };

    let filtered = filterDocumentsByDate();

    if (searchTerm) {
      filtered = filtered.filter(
        (doc) =>
          doc.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
          doc.content.toLowerCase().includes(searchTerm.toLowerCase()),
      );
    }

    setFilteredDocuments(
      filtered.slice((currentPage - 1) * pageSize, currentPage * pageSize),
    );
    setTotalPages(Math.ceil(filtered.length / pageSize));
  }, [searchTerm, documents, currentPage, pageSize, startDt, endDt]);

  useEffect(() => {
    let fpInstance: Instance | null = null;

    if (datepickerRef.current) {
      fpInstance = flatpickr(datepickerRef.current, {
        mode: "range",
        dateFormat: "Y-m-d",
        onChange: (selectedDates) => {
          if (selectedDates.length === 2) {
            setStartDt(selectedDates[0].toISOString().substring(0, 10));
            setEndDt(selectedDates[1].toISOString().substring(0, 10));
          }
        },
      });
    }

    return () => {
      if (fpInstance) {
        fpInstance.destroy();
      }
    };
  }, []);

  const handlePageChange = (page: any) => {
    setCurrentPage(page);
  };

  const handleSearchChange = (event: { target: { value: any } }) => {
    setSearchTerm(event.target.value);
  };

  const renderPagination = () => {
    let pages = [];
    for (let i = 0; i < totalPages; i++) {
      pages.push(
        <PaginationButton key={i} onClick={() => handlePageChange(i + 1)}>
          {i + 1}
        </PaginationButton>,
      );
    }
    return <PaginationButtonContainer>{pages}</PaginationButtonContainer>;
  };

  const navigateToDocument = (id: string) => {
    navigate(`/team/${teamId}/documents/${id}`);
  };

  const handleDocumentCreate = () => {
    navigate(`/team/${teamId}/documents`);
  };

  const handleCalendarClick = () => {
    navigate(`/team/${teamId}`);
  };

  const formatDate = (dateString: string | number | Date) => {
    const date = new Date(dateString);
    return date.toLocaleDateString();
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
        <DayTimeInput ref={datepickerRef} type="text" placeholder="날짜 검색" />
        <ButtonContainer>
          <StyledButton onClick={handleDocumentCreate}>문서 작성</StyledButton>
          <StyledButton onClick={handleCalendarClick}>팀 홈</StyledButton>
        </ButtonContainer>
      </InputAndButton>
      <DocumentContainer>
        {filteredDocuments.length !== 0 ? (
          filteredDocuments.map((doc) => (
            <DocumentItem
              key={doc.id}
              onClick={() => navigateToDocument(doc.id)}
            >
              <TitleContentContainer>
                <TitleDomStyled>
                  제목 : {doc.title}
                  <BlurLayer $blur={3.0} $width="5px" />
                  <BlurLayer $blur={2.6} $width="10px" />
                  <BlurLayer $blur={2.4} $width="15px" />
                  <BlurLayer $blur={2.0} $width="20px" />
                  <BlurLayer $blur={1.6} $width="25px" />
                  <BlurLayer $blur={1.2} $width="30px" />
                  <BlurLayer $blur={0.8} $width="35px" />
                  <BlurLayer $blur={0.4} $width="40px" />
                </TitleDomStyled>
                <ContentDomStyled>
                  내용 :{" "}
                  {doc.content
                    .replace(/<p>/g, "")
                    .replace(/<\/p>/g, "")
                    .replace(/<br>/g, "\n")
                    .replace(/<ol>/g, "")
                    .replace(/<\/ol>/g, "")
                    .replace(/<li>/g, "")
                    .replace(/<\/li>/g, "")
                    .replace(/<ul>/g, "")
                    .replace(/<\/ul>/g, "")
                    .replace(/<strong>/g, "")
                    .replace(/<\/strong>/g, "")
                    .replace(/<em>/g, "")
                    .replace(/<\/em>/g, "")
                    .replace(/<u>/g, "")
                    .replace(/<\/u>/g, "")
                    .replace(/<h1>/g, "")
                    .replace(/<\/h1>/g, "\n")
                    .replace(/<h2>/g, "")
                    .replace(/<\/h2>/g, "\n")
                    .replace(/<h3>/g, "")
                    .replace(/<\/h3>/g, "\n")
                    .replace(/<h4>/g, "")
                    .replace(/<\/h4>/g, "\n")}
                  <BlurLayer $blur={3.0} $width="5px" />
                  <BlurLayer $blur={2.6} $width="10px" />
                  <BlurLayer $blur={2.4} $width="15px" />
                  <BlurLayer $blur={2.0} $width="20px" />
                  <BlurLayer $blur={1.6} $width="25px" />
                  <BlurLayer $blur={1.2} $width="30px" />
                  <BlurLayer $blur={0.8} $width="35px" />
                  <BlurLayer $blur={0.4} $width="40px" />
                </ContentDomStyled>
              </TitleContentContainer>
              <DatesContainer>
                <TitleDaytime>
                  Created: {formatDate(doc.createdDt)}
                </TitleDaytime>
                <TitleDaytime>
                  Updated: {formatDate(doc.updatedDt)}
                </TitleDaytime>
              </DatesContainer>
            </DocumentItem>
          ))
        ) : (
          <span>문서가 없습니다.</span>
        )}
      </DocumentContainer>
      {renderPagination()}
    </Container>
  );
};

export default DocumentList;

const DocumentContainer = styled.div`
  box-sizing: border-box;
  width: 800px;
  height: auto;
  display: flex;
  align-content: space-between;
  flex-wrap: wrap;
  margin-bottom: 20px;
  padding-top: 30px;
`;

const DocumentItem = styled.div`
  width: 800px;
  border: 1px solid black;
  display: flex;
  justify-content: space-between;
  margin: 8px 0;
  padding: 10px;
  border-radius: 12px;
  transition:
    transform 0.3s ease,

  &:hover {
    transform: scale(1.03);
  }
`;

const TitleContentContainer = styled.div`
  flex-grow: 1;
`;

const StyledButton = styled.button`
  background-color: rgb(163, 204, 163);
  color: white;
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
  width: 800px;
  min-height: 700px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  padding-top: 50px;
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
  height: 45px;
  color: black;
  font-size: 16px;
  border: 1px solid black;
  border-radius: 8px;
  padding: 12px;
  margin-right: 4px;
`;

const TitleDomStyled = styled.h1`
  font-size: 24px;
  margin-bottom: 12px;
  font-weight: 700;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 600px;
  position: relative;

  &::after {
    content: "";
    position: absolute;
    top: 0;
    right: 0;
    width: 10%;
    height: 100%;
    box-sizing: border-box;
    backdrop-filter: blur(1px) grayscale(0);
  }
`;
interface BlurLayerProps {
  $blur: number;
  $width: string;
}

const ContentDomStyled = styled.div`
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 600px;
  position: relative;
`;

const BlurLayer = styled.div<BlurLayerProps>`
  position: absolute;
  top: 0;
  right: 0;
  height: 100%;
  box-sizing: border-box;
  width: ${(props) => props.$width};
  backdrop-filter: blur(${(props) => props.$blur}px) grayscale(0);
`;

const PaginationButton = styled.button`
  color: white;
  background-color: rgb(163, 204, 163);
  margin-right: 4px;
  height: 45px;
  width: 45px;
  display: flex;
  align-items: center;
  justify-content: center;
`;

const PaginationButtonContainer = styled.div`
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
`;

const DayTimeInput = styled.input`
  background-color: white;
  width: 100%;
  height: 45px;
  color: black;
  font-size: 16px;
  border: 1px solid black;
  border-radius: 8px;
  padding: 12px;
`;
