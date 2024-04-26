package com.ssafy.piece.domain.pieces.service;

import com.ssafy.piece.domain.cultures.entity.CultureType;
import com.ssafy.piece.domain.pieces.dto.request.PiecesAddRequestDto;
import com.ssafy.piece.domain.pieces.dto.request.RecordUpdateRequestDto;
import com.ssafy.piece.domain.pieces.dto.response.PieceRecentResponseDto;
import com.ssafy.piece.domain.pieces.dto.response.PiecesDetailResponseDto;
import com.ssafy.piece.domain.pieces.dto.response.RecordDetailResponseDto;
import com.ssafy.piece.domain.pieces.entity.GenreType;
import com.ssafy.piece.domain.pieces.entity.Pieces;
import com.ssafy.piece.domain.pieces.exception.PiecesNotFoundException;
import com.ssafy.piece.domain.pieces.exception.PiecesRecentNotFoundException;
import com.ssafy.piece.domain.pieces.repository.PiecesRepository;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PiecesService {

    private final PiecesRepository piecesRepository;

    // 조각 등록
    public Pieces addPieces(PiecesAddRequestDto piecesAddRequestDto) {
        // user 찾기
        GenreType genreType = GenreType.valueOf(String.valueOf(piecesAddRequestDto.getGenre()));

        Pieces pieces = Pieces.builder()
            .performanceType(piecesAddRequestDto.getPerformanceType())
            .title(piecesAddRequestDto.getTitle())
            .date(piecesAddRequestDto.getDate())
            .time(piecesAddRequestDto.getTime())
            .cast(piecesAddRequestDto.getCast())
            .supervision(piecesAddRequestDto.getSupervision())
            .seat(piecesAddRequestDto.getSeat())
            .price(piecesAddRequestDto.getPrice())
            .address(piecesAddRequestDto.getAddress())
            .score(piecesAddRequestDto.getScore())
            .comment(piecesAddRequestDto.getComment())
            .openYn(piecesAddRequestDto.getOpenYn())
            .imageFront(piecesAddRequestDto.getImageFront())
            .imageBack(piecesAddRequestDto.getImageBack())
            .genre(genreType)
//            .user(users)
            .build();

        return piecesRepository.save(pieces);
    }


    // 조각 상세
    public PiecesDetailResponseDto findPieceDetail(Long pieceId) {
        // user 찾기
        // 조각 권한 있는지 확인

        Pieces pieces = findById(pieceId);

        PiecesDetailResponseDto piecesDetailResponseDto = PiecesDetailResponseDto.builder()
            .pieceId(pieces.getPieceId())
            .title(pieces.getTitle())
            .frontImg(pieces.getImageFront())
            .backImg(pieces.getImageBack())
            .build();
        return piecesDetailResponseDto;
    }

    // 조각 삭제
    public void deletePiece(Long pieceId) {
        // user 찾기
        // 조각 생성자인지 확인

        piecesRepository.deleteById(pieceId);
    }

    // 기록 수정
    public void updateRecord(RecordUpdateRequestDto recordUpdateRequestDto) {
        Pieces pieces = findById(recordUpdateRequestDto.getPieceId());

        pieces.setRecord(recordUpdateRequestDto.getRecord());
        // 사진 수정
    }

    // 기록 조회
    public RecordDetailResponseDto findRecordDetail(Long pieceId) {
        Pieces pieces = findById(pieceId);

        // 사진 조회

        return RecordDetailResponseDto.builder()
            .recordId(pieces.getPieceId())
            .pieceId(pieces.getPieceId())
            .record(pieces.getRecord())
            .imgList(null)
            .build();
    }

    // 1년 전 조각 조회
    public PieceRecentResponseDto findPieceYear() {
        Long userId = 1L; // user 조회

        LocalDate dateOneYearAgo = LocalDate.now().minusYears(1);
        LocalDateTime oneYearAgoStart = dateOneYearAgo.atTime(LocalTime.MIN);
        LocalDateTime oneYearAgoEnd = dateOneYearAgo.atTime(LocalTime.MAX);

        Pieces pieces = piecesRepository.findByPieceIdAndCreatedAt(userId, oneYearAgoStart,
                oneYearAgoEnd)
            .orElseThrow(PiecesRecentNotFoundException::new);

        return PieceRecentResponseDto.builder()
            .pieceId(pieces.getPieceId())
            .frontImg(pieces.getImageFront())
            .backImg(pieces.getImageBack())
            .build();
    }


    // 조각 조회
    public Pieces findById(Long pieceId) {
        return piecesRepository.findById(pieceId)
            .orElseThrow(PiecesNotFoundException::new);
    }

    // 칭호 관련
    // 장르 3개 이상
    public boolean isGenreOver(GenreType genre, Long userId) {
        return piecesRepository.countByGenre(genre, userId) >= 3;
    }

    // 종류 5개 이상
    public boolean isPerformanceTypeOver(CultureType performanceType, Long userId) {
        return piecesRepository.countByPerformanceType(performanceType, userId) >= 5;
    }

    // 영화 시간
    public boolean isTimeMovie(LocalTime start, LocalTime end, Long userId) {
        return piecesRepository.countByTimeMovie(start, end, CultureType.MOVIE, userId) >= 1;
    }

    // 좌석
    public boolean isSeat(Long userId) {
        return piecesRepository.countBySeat("A열", userId) >= 3;
    }

    // 출연
    public boolean isCast(Long userId) {
        List<Tuple> result = piecesRepository.countPiecesByCastForUser(CultureType.CONCERT, userId);
        for (Tuple tuple : result) {
            if (tuple.get(1, Long.class).intValue() >= 3) {
                return true;
            }
        }

        return false;
    }

    // 만들기
    public boolean isPieceMake(int cnt, Long userId) {
        return piecesRepository.countByUserId(userId) >= cnt;
    }

    // 새벽
    public boolean isMidNight(LocalDateTime start, LocalDateTime end, Long userId) {
        return piecesRepository.findPiecesByCreatedAtBetween(start, end, userId) >= 3;
    }

    // 관람 당일
    public boolean isEqualDateAndCreatedAt(Long userId) {
        return piecesRepository.findPiecesByDateAndCreatedAt(userId) >= 1;
    }

    // 소비
    public boolean isConsume(Long userId) {
        return piecesRepository.sumByUserId(userId) >= 100000;
    }

    // 장르 5종류 이상
    public boolean isGenreMany() {
        Set<String> genreTypes = piecesRepository.countByGenreType();
        return genreTypes.size() >= 5;
    }
}
