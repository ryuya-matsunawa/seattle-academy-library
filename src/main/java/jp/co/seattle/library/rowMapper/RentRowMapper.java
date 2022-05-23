package jp.co.seattle.library.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

import jp.co.seattle.library.dto.RentInfo;

@Configuration
public class RentRowMapper implements RowMapper<RentInfo> {

	@Override
	public RentInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
		// Query結果（ResultSet rs）を、オブジェクトに格納する実装
		RentInfo rentInfo = new RentInfo();
		rentInfo.setBookId(rs.getInt("id"));
		rentInfo.setTitle(rs.getString("title"));
		rentInfo.setRentDate(rs.getString("rent_date"));
		rentInfo.setReturnDate(rs.getString("return_date"));
		
		return rentInfo;
	}

}
