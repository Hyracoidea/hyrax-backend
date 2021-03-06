package com.hyrax.microservice.project.service.api.impl;

import com.google.common.collect.Lists;
import com.hyrax.microservice.project.data.dao.BoardDAO;
import com.hyrax.microservice.project.data.dao.ColumnDAO;
import com.hyrax.microservice.project.data.entity.BoardEntity;
import com.hyrax.microservice.project.data.entity.ColumnEntity;
import com.hyrax.microservice.project.service.api.ColumnService;
import com.hyrax.microservice.project.service.api.impl.helper.ColumnEventEmailSenderHelper;
import com.hyrax.microservice.project.service.domain.Column;
import com.hyrax.microservice.project.service.exception.board.BoardNotFoundException;
import com.hyrax.microservice.project.service.exception.column.ColumnAdditionOperationNotAllowedException;
import com.hyrax.microservice.project.service.exception.column.ColumnAlreadyExistsException;
import com.hyrax.microservice.project.service.exception.column.ColumnRemovalOperationNotAllowedException;
import com.hyrax.microservice.project.service.exception.column.ColumnUpdateOperationNotAllowedException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ColumnServiceImpl implements ColumnService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ColumnServiceImpl.class);

    private final BoardDAO boardDao;

    private final ColumnDAO columnDAO;

    private final ModelMapper modelMapper;

    private final ColumnEventEmailSenderHelper columnEventEmailSenderHelper;

    @Override
    @Transactional(readOnly = true)
    public List<Column> findAllByBoardName(final String boardName) {
        return columnDAO.findAllByBoardName(boardName)
                .stream()
                .map(columnEntity -> modelMapper.map(columnEntity, Column.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void create(final String boardName, final String columnName, final String requestedBy) {
        final BoardEntity board = boardDao.findByBoardName(boardName).orElseThrow(() -> new BoardNotFoundException(boardName));

        if (board.getOwnerUsername().equals(requestedBy)) {
            try {
                LOGGER.info("Trying to save the column = [boardName={} columnName={}]", boardName, columnName);
                columnDAO.save(boardName, columnName);
                columnEventEmailSenderHelper.sendColumnCreationEmail(boardDao.findAllBoardMemberNameByBoardName(boardName), boardName, columnName, requestedBy);
                LOGGER.info("Column saving was successful [boardName={} columnName={}]", boardName, columnName);
            } catch (final DuplicateKeyException e) {
                final String errorMessage = String.format("Column already exists with this name=%s on this board=%s", columnName, boardName);
                LOGGER.error(errorMessage, e);
                throw new ColumnAlreadyExistsException(errorMessage);
            }
        } else {
            throw new ColumnAdditionOperationNotAllowedException(requestedBy);
        }
    }

    @Override
    @Transactional
    public void updateIndex(final String boardName, final String columnName, final String requestedBy, final long from, final long to) {

        final BoardEntity board = boardDao.findByBoardName(boardName).orElseThrow(() -> new BoardNotFoundException(boardName));

        if (board.getOwnerUsername().equals(requestedBy)) {

            final List<ColumnEntity> before = Lists.newArrayList();
            final List<ColumnEntity> after = Lists.newArrayList();
            if (from < to) {
                populateByFromLeftToRight(boardName, columnName, to, before, after);
            } else if (from > to) {
                populateByFromRightToLeft(boardName, columnName, to, before, after);
            }

            if (from != to) {
                updateColumnsByIndex(boardName, before, NumberUtils.LONG_ZERO);
                updateColumnsByIndex(boardName, after, to);
                columnDAO.updateColumnPosition(boardName, columnName, to);
            }
        } else {
            throw new ColumnUpdateOperationNotAllowedException(requestedBy);
        }
    }

    @Override
    @Transactional
    public void remove(final String boardName, final String columnName, final String requestedBy) {

        final Optional<String> ownerUsername = boardDao.findByBoardName(boardName).map(BoardEntity::getOwnerUsername);

        if (ownerUsername.isPresent() && ownerUsername.get().equals(requestedBy)) {
            columnDAO.deleteByBoardNameAndColumnName(boardName, columnName);
            refreshColumnIndexes(boardName);
            columnEventEmailSenderHelper.sendColumnRemovalEmail(boardDao.findAllBoardMemberNameByBoardName(boardName), boardName, columnName, requestedBy);
        } else {
            throw new ColumnRemovalOperationNotAllowedException(requestedBy);
        }
    }

    private void populateByFromLeftToRight(final String boardName, final String columnName, final Long newColumnIndex,
                                           final List<ColumnEntity> before, final List<ColumnEntity> after) {
        columnDAO.findAllByBoardName(boardName)
                .stream()
                .filter(column -> !columnName.equals(column.getColumnName()))
                .forEach(column -> {
                    if (column.getColumnIndex() <= newColumnIndex) {
                        before.add(column);
                    } else {
                        after.add(column);
                    }
                });
    }

    private void populateByFromRightToLeft(final String boardName, final String columnName, final Long newColumnIndex,
                                           final List<ColumnEntity> before, final List<ColumnEntity> after) {
        columnDAO.findAllByBoardName(boardName)
                .stream()
                .filter(column -> !columnName.equals(column.getColumnName()))
                .forEach(column -> {
                    if (column.getColumnIndex() < newColumnIndex) {
                        before.add(column);
                    } else {
                        after.add(column);
                    }
                });
    }

    private void updateColumnsByIndex(final String boardName, final List<ColumnEntity> columns, final Long startIndex) {
        final AtomicLong index = new AtomicLong(startIndex);

        columns.forEach(column -> columnDAO.updateColumnPosition(boardName, column.getColumnName(), index.incrementAndGet()));
    }

    private void refreshColumnIndexes(final String boardName) {
        final AtomicLong index = new AtomicLong(NumberUtils.LONG_ZERO);

        columnDAO.findAllByBoardName(boardName)
                .forEach(column -> columnDAO.updateColumnPosition(boardName, column.getColumnName(), index.incrementAndGet()));
    }
}
